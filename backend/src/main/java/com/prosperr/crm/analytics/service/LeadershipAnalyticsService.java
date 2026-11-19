package com.prosperr.crm.analytics.service;

import com.prosperr.crm.identity.domain.RoleCode;
import com.prosperr.crm.sales.api.SalesDtos.*;
import com.prosperr.crm.sales.domain.OpportunityStatus;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.*;
import java.util.*;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

/** Read-only, database-aggregated reporting. Filters are applied in SQL before group/count operations. */
@Service
public class LeadershipAnalyticsService {
    private static final ZoneId REPORTING_ZONE = ZoneId.of("Asia/Kolkata");
    private static final List<String> DEPARTMENTS = List.of("BDR", "SALES_MANAGER", "AE");
    private static final List<String> STAGES = List.of("L1", "L2", "L3", "WON", "LOST");
    private final NamedParameterJdbcTemplate jdbc;

    public LeadershipAnalyticsService(NamedParameterJdbcTemplate jdbc) { this.jdbc = jdbc; }

    public LeadershipFilterOptions filterOptions() {
        List<AnalyticsEmployeeView> employees = jdbc.query("select id, full_name, role_code from app_user where role_code in ('BDR','SALES_MANAGER','AE') order by role_code, full_name",
                new MapSqlParameterSource(), (rs, row) -> new AnalyticsEmployeeView(rs.getObject("id", UUID.class), rs.getString("full_name"), rs.getString("role_code")));
        return new LeadershipFilterOptions(employees, DEPARTMENTS, STAGES);
    }

    public LeadershipOverviewView overview(AnalyticsFilter input) {
        Filter f = filter(input);
        long organisations = count("select count(o.id) from organisation o join app_user u on u.id=o.created_by" + where(f, "o.created_at", "o.created_by", "u"), f);
        long contacts = count("select count(c.id) from contact c join app_user u on u.id=c.created_by" + where(f, "c.created_at", "c.created_by", "u"), f);
        long leads = count("select count(l.id) from sales_lead l join app_user u on u.id=l.owner_id" + where(f, "l.created_at", "l.owner_id", "u"), f);
        long sqls = count("select count(q.id) from sql_qualification q join app_user u on u.id=q.created_by" + where(f, "q.submitted_at", "q.created_by", "u") + " and q.status <> 'DRAFT'", f);
        String oppWhere = where(f, "o.assigned_at", "o.assigned_ae_id", "u") + stagePredicate(f, "o.status");
        long opportunityCount = count("select count(o.id) from sales_opportunity o join app_user u on u.id=o.assigned_ae_id" + oppWhere, f);
        long won = count("select count(o.id) from sales_opportunity o join app_user u on u.id=o.assigned_ae_id" + where(f, "o.closed_at", "o.assigned_ae_id", "u") + stagePredicate(f, "o.status") + " and o.status='WON'", f);
        long lost = count("select count(o.id) from sales_opportunity o join app_user u on u.id=o.assigned_ae_id" + where(f, "o.closed_at", "o.assigned_ae_id", "u") + stagePredicate(f, "o.status") + " and o.status='LOST'", f);
        BigDecimal revenue = decimal("select coalesce(sum(o.final_deal_value),0) from sales_opportunity o join app_user u on u.id=o.assigned_ae_id" + where(f, "o.closed_at", "o.assigned_ae_id", "u") + stagePredicate(f, "o.status") + " and o.status='WON'", f);
        return new LeadershipOverviewView(organisations, contacts, leads, sqls, opportunityCount, won, lost, revenue, "INR");
    }

    public List<BdrAnalyticsView> bdrs(AnalyticsFilter input) {
        Filter f = filter(input);
        if (!roleSelected(f, RoleCode.BDR)) return List.of();
        String usersWhere = employeeWhere(f, "u");
        String orgTime = time(f, "o.created_at"), contactTime = time(f, "c.created_at"), callTime = time(f, "ca.called_at"), sqlTime = time(f, "q.submitted_at");
        String sql = "select u.id, u.full_name, "
                + "(select count(*) from organisation o where o.created_by=u.id" + orgTime + ") orgs, "
                + "(select count(*) from contact c where c.created_by=u.id" + contactTime + ") contacts, "
                + "(select count(*) from call_activity ca where ca.created_by=u.id" + callTime + ") calls, "
                + "(select count(distinct ca.lead_id) from call_activity ca where ca.created_by=u.id and ca.outcome='INTERESTED'" + callTime + ") interested, "
                + "(select count(*) from sql_qualification q where q.created_by=u.id and q.status<>'DRAFT'" + sqlTime + ") submitted, "
                + "(select count(*) from sql_qualification q where q.created_by=u.id and q.status='APPROVED'" + sqlTime + ") approved "
                + "from app_user u where u.role_code='BDR'" + usersWhere + " order by u.full_name";
        return jdbc.query(sql, params(f), (rs, row) -> {
            long submitted = rs.getLong("submitted"), approved = rs.getLong("approved");
            return new BdrAnalyticsView(rs.getObject("id", UUID.class), rs.getString("full_name"), rs.getLong("orgs"), rs.getLong("contacts"),
                    rs.getLong("calls"), rs.getLong("interested"), submitted, approved, percent(approved, submitted));
        });
    }

    public List<ManagerAnalyticsView> managers(AnalyticsFilter input) {
        Filter f = filter(input);
        if (!roleSelected(f, RoleCode.SALES_MANAGER)) return List.of();
        String usersWhere = employeeWhere(f, "u");
        String reviewedTime = time(f, "q.reviewed_at");
        String allocationsTime = time(f, "h.created_at");
        String approvedTime = time(f, "q.reviewed_at");
        String sql = "select u.id,u.full_name,"
                + "(select count(*) from sql_qualification q where q.reviewed_by=u.id and q.status in ('APPROVED','REJECTED')" + reviewedTime + ") reviewed,"
                + "(select count(*) from sql_qualification q where q.reviewed_by=u.id and q.status='APPROVED'" + approvedTime + ") approved,"
                + "(select count(distinct h.opportunity_id) from opportunity_assignment_history h where h.assigned_by=u.id and h.action='ASSIGNED'" + allocationsTime + ") allocated "
                + "from app_user u where u.role_code='SALES_MANAGER'" + usersWhere + " order by u.full_name";
        List<ManagerAnalyticsView> base = jdbc.query(sql, params(f), (rs, row) -> {
            long reviewed = rs.getLong("reviewed"), approved = rs.getLong("approved");
            return new ManagerAnalyticsView(rs.getObject("id", UUID.class), rs.getString("full_name"), reviewed, approved,
                    percent(approved, reviewed), BigDecimal.ZERO, rs.getLong("allocated"), null);
        });
        Map<UUID, BigDecimal> assignmentHours = averageAssignmentHours(f);
        return base.stream().map(m -> new ManagerAnalyticsView(m.userId(), m.fullName(), m.sqlsReviewed(), m.sqlsApproved(),
                m.sqlApprovalRate(), assignmentHours.getOrDefault(m.userId(), BigDecimal.ZERO), m.opportunitiesAllocated(), null)).toList();
    }

    public List<AeAnalyticsView> aes(AnalyticsFilter input) {
        Filter f = filter(input);
        if (!roleSelected(f, RoleCode.AE)) return List.of();
        String scope = employeeWhere(f, "u");
        String assignedTime = time(f, "o.assigned_at");
        String stage = stagePredicate(f, "o.status");
        String revenueSubquery = f.stage() != null && f.stage() != OpportunityStatus.WON ? "0 revenue" :
                "(select coalesce(sum(r.final_deal_value),0) from sales_opportunity r where r.assigned_ae_id=u.id and r.status='WON'" + time(f,"r.closed_at") + ") revenue";
        String sql = "select u.id,u.full_name, count(o.id) total,"
                + "sum(case when o.status='L1' then 1 else 0 end) l1, sum(case when o.status='L2' then 1 else 0 end) l2,"
                + "sum(case when o.status='L3' then 1 else 0 end) l3, sum(case when o.status='WON' then 1 else 0 end) won,"
                + "sum(case when o.status='LOST' then 1 else 0 end) lost, " + revenueSubquery + " "
                + "from app_user u left join sales_opportunity o on o.assigned_ae_id=u.id" + assignedTime + stage
                + " where u.role_code='AE'" + scope + " group by u.id,u.full_name order by u.full_name";
        return jdbc.query(sql, params(f), (rs, row) -> {
            long won=rs.getLong("won"), lost=rs.getLong("lost");
            BigDecimal revenue=rs.getBigDecimal("revenue");
            return new AeAnalyticsView(rs.getObject("id",UUID.class),rs.getString("full_name"),rs.getLong("total"),rs.getLong("l1"),rs.getLong("l2"),rs.getLong("l3"),won,lost,percent(won,won+lost),revenue,"INR");
        });
    }

    public List<FunnelStepView> funnel(AnalyticsFilter input) {
        Filter f = filter(input);
        List<FunnelStepView> result = new ArrayList<>();
        result.add(new FunnelStepView("Organisation Discovery", count("select count(o.id) from organisation o join app_user u on u.id=o.created_by" + where(f,"o.created_at","o.created_by","u"), f)));
        result.add(new FunnelStepView("Contact Mining", count("select count(c.id) from contact c join app_user u on u.id=c.created_by" + where(f,"c.created_at","c.created_by","u"), f)));
        result.add(new FunnelStepView("Calling", count("select count(ca.id) from call_activity ca join app_user u on u.id=ca.created_by" + where(f,"ca.called_at","ca.created_by","u"), f)));
        result.add(new FunnelStepView("Interested Prospects", count("select count(distinct ca.lead_id) from call_activity ca join app_user u on u.id=ca.created_by" + where(f,"ca.called_at","ca.created_by","u") + " and ca.outcome='INTERESTED'", f)));
        result.add(new FunnelStepView("SQL", count("select count(q.id) from sql_qualification q join app_user u on u.id=q.created_by" + where(f,"q.submitted_at","q.created_by","u") + " and q.status<>'DRAFT'", f)));
        String opWhere=where(f,"o.assigned_at","o.assigned_ae_id","u")+stagePredicate(f,"o.status");
        result.add(new FunnelStepView("AE Assignment", count("select count(o.id) from sales_opportunity o join app_user u on u.id=o.assigned_ae_id"+opWhere, f)));
        for (String stage : STAGES) result.add(new FunnelStepView(stage, count("select count(o.id) from sales_opportunity o join app_user u on u.id=o.assigned_ae_id"+where(f,"o.assigned_at","o.assigned_ae_id","u")+stagePredicate(f,"o.status")+" and o.status='"+stage+"'", f)));
        return result;
    }

    public LeadershipRevenueView revenue(AnalyticsFilter input) {
        Filter f = filter(input);
        if (f.stage() != null && f.stage() != OpportunityStatus.WON) return new LeadershipRevenueView(List.of(),List.of(),List.of(),List.of(),"INR");
        String condition = where(f,"o.closed_at","o.assigned_ae_id","u") + stagePredicate(f,"o.status") + " and o.status='WON'";
        String monthlySql = "select extract(year from o.closed_at) yr, extract(month from o.closed_at) period, sum(o.final_deal_value) amount from sales_opportunity o join app_user u on u.id=o.assigned_ae_id"+condition+" group by extract(year from o.closed_at),extract(month from o.closed_at) order by yr,period";
        String quarterlySql = "select extract(year from o.closed_at) yr, ((extract(month from o.closed_at)-1)/3)+1 period, sum(o.final_deal_value) amount from sales_opportunity o join app_user u on u.id=o.assigned_ae_id"+condition+" group by extract(year from o.closed_at),((extract(month from o.closed_at)-1)/3)+1 order by yr,period";
        List<RevenuePointView> monthly=jdbc.query(monthlySql,params(f),(rs,row)->new RevenuePointView(String.format("%04d-%02d",rs.getInt("yr"),rs.getInt("period")),rs.getBigDecimal("amount")));
        List<RevenuePointView> quarterly=jdbc.query(quarterlySql,params(f),(rs,row)->new RevenuePointView(rs.getInt("yr")+" Q"+rs.getInt("period"),rs.getBigDecimal("amount")));
        List<RevenueByAeView> byAe=jdbc.query("select u.id,u.full_name,count(o.id) wins,coalesce(sum(o.final_deal_value),0) amount from app_user u left join sales_opportunity o on o.assigned_ae_id=u.id and o.status='WON'"+time(f,"o.closed_at")+" where u.role_code='AE'"+employeeWhere(f,"u")+" group by u.id,u.full_name order by amount desc",params(f),(rs,row)->new RevenueByAeView(rs.getObject("id",UUID.class),rs.getString("full_name"),rs.getLong("wins"),rs.getBigDecimal("amount")));
        List<RevenueBySourceView> bySource=jdbc.query("select coalesce(nullif(trim(org.lead_source),''),'Unspecified') source,count(o.id) wins,coalesce(sum(o.final_deal_value),0) amount from sales_opportunity o join app_user u on u.id=o.assigned_ae_id join sql_qualification q on q.id=o.sql_qualification_id join sales_lead l on l.id=q.lead_id join organisation org on org.id=l.organisation_id"+condition+" group by coalesce(nullif(trim(org.lead_source),''),'Unspecified') order by amount desc",params(f),(rs,row)->new RevenueBySourceView(rs.getString("source"),rs.getLong("wins"),rs.getBigDecimal("amount")));
        return new LeadershipRevenueView(monthly,quarterly,byAe,bySource,"INR");
    }

    private Map<UUID,BigDecimal> averageAssignmentHours(Filter f) {
        String sql="select u.id manager_id, q.reviewed_at, first_assignment.assigned_at from app_user u join sql_qualification q on q.reviewed_by=u.id join sales_opportunity o on o.sql_qualification_id=q.id join (select opportunity_id,min(created_at) assigned_at from opportunity_assignment_history where action='ASSIGNED' group by opportunity_id) first_assignment on first_assignment.opportunity_id=o.id where u.role_code='SALES_MANAGER' and q.status='APPROVED'"+time(f,"q.reviewed_at")+employeeWhere(f,"u");
        Map<UUID,List<Duration>> values=new HashMap<>();
        jdbc.query(sql,params(f),rs->{ UUID id=rs.getObject("manager_id",UUID.class); Instant start=rs.getTimestamp("reviewed_at").toInstant(); Instant end=rs.getTimestamp("assigned_at").toInstant(); values.computeIfAbsent(id,k->new ArrayList<>()).add(Duration.between(start,end)); });
        Map<UUID,BigDecimal> averages=new HashMap<>(); values.forEach((id,list)->{ long total=list.stream().mapToLong(Duration::toSeconds).sum(); averages.put(id,BigDecimal.valueOf(total).divide(BigDecimal.valueOf(list.size()*3600L),2,RoundingMode.HALF_UP)); });
        return averages;
    }

    private Filter filter(AnalyticsFilter input) {
        AnalyticsFilter x=input==null?new AnalyticsFilter(null,null,null,null,null,null,null,null):input;
        if(x.month()!=null&&(x.month()<1||x.month()>12))bad("Month must be between 1 and 12.");
        if(x.quarter()!=null&&(x.quarter()<1||x.quarter()>4))bad("Quarter must be between 1 and 4.");
        if(x.year()!=null&&(x.year()<1900||x.year()>2100))bad("Year must be between 1900 and 2100.");
        if(x.month()!=null&&x.quarter()!=null)bad("Choose a month or quarter, not both.");
        if(x.fromDate()!=null&&x.toDate()!=null&&x.toDate().isBefore(x.fromDate()))bad("End date must be on or after start date.");
        String department=x.department()==null||x.department().isBlank()?null:x.department().trim().toUpperCase(Locale.ROOT);
        if(department!=null&&!DEPARTMENTS.contains(department))bad("Department must be BDR, SALES_MANAGER or AE.");
        OpportunityStatus stage=null;
        if(x.stage()!=null&&!x.stage().isBlank())try{stage=OpportunityStatus.valueOf(x.stage().trim().toUpperCase(Locale.ROOT));}catch(IllegalArgumentException e){bad("Stage must be L1, L2, L3, WON or LOST.");}
        LocalDate from=x.fromDate(),toExclusive=x.toDate()==null?null:x.toDate().plusDays(1);
        int year=x.year()==null?Year.now(REPORTING_ZONE).getValue():x.year();
        if(x.month()!=null){LocalDate s=LocalDate.of(year,x.month(),1);from=max(from,s);toExclusive=min(toExclusive,s.plusMonths(1));}
        if(x.quarter()!=null){int firstMonth=(x.quarter()-1)*3+1;LocalDate s=LocalDate.of(year,firstMonth,1);from=max(from,s);toExclusive=min(toExclusive,s.plusMonths(3));}
        return new Filter(from==null?null:from.atStartOfDay(REPORTING_ZONE).toInstant(),toExclusive==null?null:toExclusive.atStartOfDay(REPORTING_ZONE).toInstant(),department,x.employeeId(),stage);
    }
    private LocalDate max(LocalDate a,LocalDate b){return a==null||a.isBefore(b)?b:a;}
    private LocalDate min(LocalDate a,LocalDate b){return a==null||a.isAfter(b)?b:a;}
    private boolean roleSelected(Filter f,RoleCode role){return f.department()==null||f.department().equals(role.name());}
    private String employeeWhere(Filter f,String userAlias){return f.employeeId()==null?"":" and "+userAlias+".id=:employeeId";}
    private String time(Filter f,String dateColumn){return (f.from()==null?"":" and "+dateColumn+">=:fromAt")+(f.to()==null?"":" and "+dateColumn+"<:toAt");}
    private String where(Filter f,String dateColumn,String userIdColumn,String userAlias){return " where 1=1"+time(f,dateColumn)+employeeWhere(f,userAlias)+(f.department()==null?"":" and "+userAlias+".role_code=:department");}
    private String stagePredicate(Filter f,String stageColumn){return f.stage()==null?"":" and "+stageColumn+"=:stage";}
    private MapSqlParameterSource params(Filter f){MapSqlParameterSource p=new MapSqlParameterSource();if(f.from()!=null)p.addValue("fromAt",Timestamp.from(f.from()));if(f.to()!=null)p.addValue("toAt",Timestamp.from(f.to()));if(f.department()!=null)p.addValue("department",f.department());if(f.employeeId()!=null)p.addValue("employeeId",f.employeeId());if(f.stage()!=null)p.addValue("stage",f.stage().name());return p;}
    private long count(String sql,Filter f){Long value=jdbc.queryForObject(sql,params(f),Long.class);return value==null?0:value;}
    private BigDecimal decimal(String sql,Filter f){return decimal(sql,f,null);}
    private BigDecimal decimal(String sql,Filter f,UUID employeeId){MapSqlParameterSource p=params(f);if(employeeId!=null)p.addValue("employeeId",employeeId);BigDecimal value=jdbc.queryForObject(sql,p,BigDecimal.class);return value==null?BigDecimal.ZERO:value;}
    private BigDecimal percent(long numerator,long denominator){return denominator==0?BigDecimal.ZERO:BigDecimal.valueOf(numerator).multiply(BigDecimal.valueOf(100)).divide(BigDecimal.valueOf(denominator),1,RoundingMode.HALF_UP);}
    private void bad(String message){throw new ResponseStatusException(HttpStatus.BAD_REQUEST,message);}
    private record Filter(Instant from,Instant to,String department,UUID employeeId,OpportunityStatus stage){}
}
