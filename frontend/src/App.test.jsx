import { afterEach, describe, expect, it, vi } from "vitest";
import { cleanup, render, screen } from "@testing-library/react";
import { MemoryRouter } from "react-router-dom";
import { AppRoutes } from "@/App.jsx";
const auth = vi.hoisted(() => ({ user: null, login: vi.fn(), logout: vi.fn(), loading: false, changePassword: vi.fn() }));
vi.mock("@/auth/AuthContext", () => ({ useAuth: () => auth, AuthProvider: ({children}) => children }));
afterEach(() => { cleanup(); auth.user = null; });
describe("authentication routes", () => {
 it("redirects unauthenticated visitors to sign in", () => { render(<MemoryRouter initialEntries={["/admin"]}><AppRoutes/></MemoryRouter>); expect(screen.getByRole("heading",{name:"Sign in to Sales CRM"})).toBeInTheDocument(); });
 it("shows the email and password sign-in form", () => { render(<MemoryRouter initialEntries={["/login"]}><AppRoutes/></MemoryRouter>); expect(screen.getByLabelText("Work email")).toBeInTheDocument(); expect(screen.getByLabelText("Password")).toBeInTheDocument(); });
 it("redirects signed-in users with a temporary password to password change", () => { auth.user={id:"u1",role:"ADMIN",fullName:"Admin",mustChangePassword:true}; render(<MemoryRouter initialEntries={["/admin"]}><AppRoutes/></MemoryRouter>); expect(screen.getByRole("heading",{name:"Set a new password"})).toBeInTheDocument(); });
});
