import { Bar, BarChart, CartesianGrid, Legend, ResponsiveContainer, Tooltip, XAxis, YAxis } from "recharts";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";

const numberFormatter = new Intl.NumberFormat("en-IN", { notation: "compact", maximumFractionDigits: 1 });

export function ChartContainer({ title, description, data, series }) {
  return (
    <Card className="min-w-0">
      <CardHeader className="pb-1">
        <CardTitle>{title}</CardTitle>
        <CardDescription>{description}</CardDescription>
      </CardHeader>
      <CardContent className="pt-4">
        <div className="h-[260px] w-full" role="img" aria-label={`${title}. ${description}`}>
          <ResponsiveContainer width="100%" height="100%">
            <BarChart data={data} margin={{ top: 8, right: 4, left: -18, bottom: 0 }} barGap={5}>
              <CartesianGrid vertical={false} stroke="#e9eef5" strokeDasharray="3 3" />
              <XAxis dataKey="month" axisLine={false} tickLine={false} tick={{ fill: "#94a3b8", fontSize: 11 }} dy={8} />
              <YAxis axisLine={false} tickLine={false} tick={{ fill: "#94a3b8", fontSize: 10 }} tickFormatter={numberFormatter.format} />
              <Tooltip
                cursor={{ fill: "#f8fafc" }}
                contentStyle={{ borderRadius: 10, border: "1px solid #e2e8f0", boxShadow: "0 6px 20px rgba(15,23,42,.08)", fontSize: 12 }}
                formatter={(value, name) => [numberFormatter.format(value), series.find((item) => item.key === name)?.label ?? name]}
              />
              <Legend iconType="circle" iconSize={7} wrapperStyle={{ fontSize: 11, color: "#64748b", paddingTop: 12 }} formatter={(key) => series.find((item) => item.key === key)?.label ?? key} />
              {series.map((item) => <Bar key={item.key} dataKey={item.key} fill={item.color} radius={[4, 4, 0, 0]} maxBarSize={28} />)}
            </BarChart>
          </ResponsiveContainer>
        </div>
      </CardContent>
    </Card>
  );
}
