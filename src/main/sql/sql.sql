CREATE TABLE "public"."t_company" (
"term_start" varchar(255),
"term_end" varchar(255),
"checked" varchar(255),
"company_id" varchar(255),
"company_name" text,
"regist_no" int8,
"belong_org" text,
"oper_name" text,
"started" varchar(255),
"ended" varchar(255),
"status" text,
"province" text,
"updated" timestamp(0),
"credit_code" text,
"regist_capi" float8,
"econ_kind" text,
"address" text,
"scope" text,
"regist_capi_desc" varchar(255),
"url" text,
"phone_number" varchar(255),
"email" varchar(255),
"mobile" varchar(255)
);


insert overwrite table tmp_company
select t.term_start ,t.term_end,t.check_date checked,t.company_id,t.company_name,t.regist_no,t.belong_org,t.oper_name,t.start_date started,t.end_date ended,
       t.status,t.province,t.updated_date updated,t.credit_code,t.regist_capi,t.econ_kind,t.address,t.scope,t.regist_capi_desc,
       COALESCE(t4.website,"") AS url,COALESCE(t1.office_phone,"") AS phone_number,COALESCE(t1.email,"") AS email,COALESCE(t1.mobile,"") AS mobile
from t_company t
left join t_company_contact t1 on t.company_id = t1.company_id
left join (select t2.company_id,concat_ws(' ',collect_set(t3.website)) website
from t_annual_report t2
left join t_annual_website t3 on t2.annual_report_id = t3.annual_report_id
group by t2.company_id) t4 on t.company_id = t4.company_id