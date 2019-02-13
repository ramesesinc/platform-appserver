SELECT 
    ugm.objid, ugm.user_username, ugm.user_lastname, ugm.user_firstname, 
    ugm.org_name, sg.name AS securitygroup_name 
FROM sys_usergroup ug 
    INNER JOIN sys_usergroup_member ugm ON ug.objid=ugm.usergroup_objid 
    LEFT JOIN sys_securitygroup sg ON ugm.securitygroup_objid=sg.objid  
WHERE ug.domain='treasury' 
ORDER BY ugm.user_lastname, ugm.user_firstname