const BASE_URL = import.meta.env.VITE_API_URL ?? "http://localhost:8082";
export class ApiError extends Error { status:number; body:unknown; constructor(status:number, message:string, body:unknown){ super(message); this.status=status; this.body=body; } }
type Query=Record<string,string|number|boolean|null|undefined>;
async function request<T>(method:string,path:string,body?:unknown,query?:Query):Promise<T>{
  const url=new URL(BASE_URL+path); Object.entries(query??{}).forEach(([k,v])=>{if(v!==undefined&&v!==null&&v!=="")url.searchParams.set(k,String(v))});
  const res=await fetch(url,{method,headers:{"Content-Type":"application/json"},body:body===undefined?undefined:JSON.stringify(body)});
  if(!res.ok){let payload:unknown;try{payload=await res.json()}catch{payload=await res.text()}const obj=payload as {mensagem?:string;message?:string;erro?:string};throw new ApiError(res.status,obj?.mensagem??obj?.message??obj?.erro??`HTTP ${res.status}`,payload)}
  if(res.status===204)return undefined as T; return res.json() as Promise<T>;
}
export const api={get:<T>(p:string,q?:Query)=>request<T>("GET",p,undefined,q),post:<T>(p:string,b?:unknown,q?:Query)=>request<T>("POST",p,b,q),put:<T>(p:string,b?:unknown,q?:Query)=>request<T>("PUT",p,b,q),patch:<T>(p:string,b?:unknown,q?:Query)=>request<T>("PATCH",p,b,q),delete:<T=void>(p:string,q?:Query)=>request<T>("DELETE",p,undefined,q)};
