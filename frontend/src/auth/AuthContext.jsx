import { createContext,useContext,useEffect,useMemo,useState } from "react";
import { api,setAccessToken } from "@/lib/api";
const C=createContext(null);
export function AuthProvider({children}){const [user,setUser]=useState(null),[loading,setLoading]=useState(true);
 useEffect(()=>{setLoading(false);},[]);
 async function login(email,password){const {data}=await api.post("/auth/login",{email,password});setAccessToken(data.accessToken);setUser(data.user);return data.user;}
 async function changePassword(currentPassword,newPassword){const {data}=await api.post("/auth/change-password",{currentPassword,newPassword});setAccessToken(data.accessToken);setUser(data.user);return data.user;}
 function logout(){setAccessToken(null);setUser(null);}
 const value=useMemo(()=>({user,loading,login,logout,changePassword}),[user,loading]);return <C.Provider value={value}>{children}</C.Provider>;
}
export function useAuth(){return useContext(C);}
