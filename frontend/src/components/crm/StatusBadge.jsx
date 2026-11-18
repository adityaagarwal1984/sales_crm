import { Badge } from "@/components/ui/badge";

const statusTone = {
  "In review": "blue", "In progress": "blue", "Confirmed": "blue", "New": "violet",
  "Completed": "green", "On track": "green", "Ahead": "green", "Due today": "amber",
  "Needs info": "amber", "Needs prep": "amber", "Scheduled": "neutral", "Needs attention": "red",
};

export function StatusBadge({ status }) {
  return <Badge tone={statusTone[status] ?? "neutral"}>{status}</Badge>;
}
