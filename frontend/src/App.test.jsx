import { afterEach, describe, expect, it } from "vitest";
import { cleanup, render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { MemoryRouter } from "react-router-dom";
import { AppRoutes } from "@/App.jsx";

afterEach(cleanup);

describe("role dashboard navigation", () => {
  it.each([
    ["/admin", "Admin dashboard"],
    ["/bdr", "BDR dashboard"],
    ["/sales-manager", "Sales manager dashboard"],
    ["/ae", "Account executive dashboard"],
    ["/leadership", "Leadership overview"],
  ])("renders the dashboard at %s", (path, heading) => {
    render(<MemoryRouter initialEntries={[path]}><AppRoutes /></MemoryRouter>);
    expect(screen.getByRole("heading", { name: heading })).toBeInTheDocument();
    expect(screen.getByText(/illustrative placeholders/i)).toBeInTheDocument();
  });

  it("switches from one role dashboard to another through the profile menu", async () => {
    const user = userEvent.setup();
    render(<MemoryRouter initialEntries={["/admin"]}><AppRoutes /></MemoryRouter>);

    const destinations = [
      ["Business Development", "BDR dashboard"],
      ["Sales Manager", "Sales manager dashboard"],
      ["Account Executive", "Account executive dashboard"],
      ["Leadership", "Leadership overview"],
      ["Administrator", "Admin dashboard"],
    ];

    for (const [role, heading] of destinations) {
      await user.click(screen.getByRole("button", { name: "Open demo profile menu" }));
      await user.click(screen.getByRole("menuitem", { name: `Switch to ${role}` }));
      expect(screen.getByRole("heading", { name: heading })).toBeInTheDocument();
    }
  });

  it("filters the current dashboard's illustrative records from the top-bar search", async () => {
    const user = userEvent.setup();
    render(<MemoryRouter initialEntries={["/bdr"]}><AppRoutes /></MemoryRouter>);

    await user.type(screen.getByRole("searchbox", { name: "Search current dashboard records" }), "Northstar");
    expect(screen.getByText("Northstar Labs")).toBeInTheDocument();
    expect(screen.queryByText("BluePeak Systems")).not.toBeInTheDocument();
  });
});
