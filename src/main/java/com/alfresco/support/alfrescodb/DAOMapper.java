package com.alfresco.support.alfrescodb;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.alfresco.support.alfrescodb.beans.AccessControlBean;
import com.alfresco.support.alfrescodb.beans.ActivitiesFeedByApplication;
import com.alfresco.support.alfrescodb.beans.ActivitiesFeedByTypeBean;
import com.alfresco.support.alfrescodb.beans.ActivitiesFeedByUserBean;
import com.alfresco.support.alfrescodb.beans.AppliedPatchesBean;
import com.alfresco.support.alfrescodb.beans.ArchivedNodesBean;
import com.alfresco.support.alfrescodb.beans.ContentModelBean;
import com.alfresco.support.alfrescodb.beans.DbMSSQLBean;
import com.alfresco.support.alfrescodb.beans.DbMySQLBean;
import com.alfresco.support.alfrescodb.beans.DbOracleIndexBean;
import com.alfresco.support.alfrescodb.beans.DbOracleTableBean;
import com.alfresco.support.alfrescodb.beans.DbPostgresBean;
import com.alfresco.support.alfrescodb.beans.JmxPropertiesBean;
import com.alfresco.support.alfrescodb.beans.LargeFolderBean;
import com.alfresco.support.alfrescodb.beans.LargeTransactionBean;
import com.alfresco.support.alfrescodb.beans.LockedResourcesBean;
import com.alfresco.support.alfrescodb.beans.NodeContentTypeBean;
import com.alfresco.support.alfrescodb.beans.NodeContentTypeMonthBean;
import com.alfresco.support.alfrescodb.beans.NodeMimeTypeBean;
import com.alfresco.support.alfrescodb.beans.NodeStoreBean;
import com.alfresco.support.alfrescodb.beans.WorkflowBean;

@Mapper
public interface DAOMapper {
    /*
     * Applied Patches
     */
    @Select("SELECT id, applied_to_schema appliedToSchema, applied_on_date appliedOnDate, " +
            "applied_to_server appliedToServer, was_executed wasExecuted, succeeded, report " +
            "FROM alf_applied_patch")
    List<AppliedPatchesBean> listAppliedPatches();

    /*
     * DB Size
     */
    // Postgres Queries
    @Select("SELECT pg_namespace.nspname schemaName, pg_class.relname tableName, cast(pg_class.reltuples as int8) rowEstimates, " +
            "pg_relation_size(pg_catalog.pg_class.oid) tableSizeBytes, pg_size_pretty(pg_relation_size(pg_catalog.pg_class.oid)) prettySize, "
            +
            "pg_indexes_size(pg_class.oid) AS indexSizeBytes, stats.last_vacuum as lastVacuum, stats.last_autovacuum as lastAutoVacuum, " +
            "stats.last_analyze as lastAnalyze, stats.last_autoanalyze as lastAutoAnalyze " +
            "FROM pg_catalog.pg_class " +
            "JOIN pg_catalog.pg_namespace ON relnamespace = pg_catalog.pg_namespace.oid " +
            "left JOIN pg_catalog.pg_stat_all_tables stats on pg_class.oid = stats.relid " +
            "WHERE pg_namespace.nspname NOT LIKE 'pg_%' ")
    List<DbPostgresBean> findTablesInfoPostgres();

    // MySQL Queries/
    @Select("SELECT table_schema as schemaName, table_name as tableName, engine, " +
            "data_length as tableSizeBytes, index_length as indexSizeBytes, table_rows as rowEstimates, " +
            "(data_length +index_length) as totalSizeBytes " +
            "FROM information_schema.TABLES " +
            "where table_schema not in ('sys','performance_schema','information_schema','mysql') ")
    List<DbMySQLBean> findTablesInfoMysql();

    // Oracle Queries
    @Select("SELECT ut.TABLE_NAME tableName, us.BYTES, ut.NUM_ROWS numRows, ut.LAST_ANALYZED lastAnalyzed, ut.AVG_ROW_LEN averageRowLength " +
            "FROM USER_SEGMENTS us " +
            "inner join USER_TABLES ut on us.SEGMENT_NAME = ut.TABLE_NAME " +
            "where us.SEGMENT_TYPE in ('TABLE')")
    List<DbOracleTableBean> findTablesInfoOracle();

    @Select("SELECT ui.INDEX_NAME indexName, us.BYTES, ui.TABLE_NAME tableName, ui.LAST_ANALYZED lastAnalyzed, ui.NUM_ROWS numRows, ui.DISTINCT_KEYS distinctKeys, ui.status " +
            "FROM USER_SEGMENTS us " +
            "inner join USER_INDEXES ui on us.SEGMENT_NAME = ui.INDEX_NAME " +
            "where us.SEGMENT_TYPE in ('INDEX') ")
    List<DbOracleIndexBean> findIndexesInfoOracle();

    // MS SQL Queries
    @Select("SELECT \n" +
            " s.Name as SchemaName, t.NAME AS TableName, p.rows AS RowCounts,\n" +
            " (SUM(a.total_pages) * 8 * 1024) AS TotalSpace, \n" +
            " (SUM(a.used_pages) * 8 * 1024) AS UsedSpace, \n" +
            " ((SUM(a.total_pages) - SUM(a.used_pages)) * 8 *1024 ) AS UnusedSpace \n" +
            "FROM sys.tables t \n" +
            "INNER JOIN sys.schemas s ON s.schema_id = t.schema_id \n" +
            "INNER JOIN sys.indexes i ON t.OBJECT_ID = i.object_id \n" +
            "INNER JOIN sys.partitions p ON i.object_id = p.OBJECT_ID AND i.index_id = p.index_id \n" +
            "INNER JOIN sys.allocation_units a ON p.partition_id = a.container_id \n" +
            "WHERE \n" +
            " t.NAME NOT LIKE 'dt%'    -- filter out system tables for diagramming \n" +
            " AND t.is_ms_shipped = 0 \n" +
            " AND i.OBJECT_ID > 255 \n" +
            "GROUP BY \n" +
            " s.Name, t.Name, s.Name, p.Rows")
    List<DbMSSQLBean> findTablesInfoMSSql();

    @Select("SELECT\n" +
            " s.Name as SchemaName, OBJECT_NAME(i.OBJECT_ID) AS TableName,\n" +
            " i.name AS IndexName,\n" +
            " i.index_id AS IndexID,\n" +
            " (8 * SUM(a.used_pages) * 1024) AS 'IndexSize'\n" +
            "FROM sys.indexes AS i\n" +
            "JOIN sys.partitions AS p ON p.OBJECT_ID = i.OBJECT_ID AND p.index_id = i.index_id \n" +
            "JOIN sys.allocation_units AS a ON a.container_id = p.partition_id \n" +
            "JOIN sys.tables t ON t.OBJECT_ID = i.object_id \n" +
            "JOIN sys.schemas s ON s.schema_id = t.schema_id \n" +
            "GROUP BY i.OBJECT_ID,i.index_id,i.name")
    List<DbMSSQLBean> findIndexesInfoMSSql();

    /*
     * Large Folders
     */
    @Select("SELECT count(*) as count, alf_store.protocol, alf_store.identifier, alf_node.uuid, " +
            "  alf_node_properties.string_value as nodeName, alf_qname.local_name as localName " +
            "FROM alf_node, alf_store, alf_child_assoc, alf_node_properties, alf_qname " +
            "WHERE alf_child_assoc.parent_node_id=alf_node.id " +
            "  and alf_store.id=alf_node.store_id " +
            "  and alf_node_properties.node_id = alf_node.id " +
            "  and alf_node_properties.qname_id IN (SELECT id FROM alf_qname WHERE local_name = 'name') " +
            "  and alf_qname.id = alf_node.type_qname_id " +
            "GROUP BY alf_store.protocol,alf_store.identifier,alf_node.uuid, " +
            "  alf_node_properties.string_value,alf_qname.local_name " +
            "HAVING count(*) > #{size} ")
    List<LargeFolderBean> findLargeFolders(@Param("size") int size);

    /*
     * Large Transactions
     */
    @Select("SELECT trx.id as transactionId, count(*) as count " +
            "FROM alf_transaction trx, alf_node an " +
            "WHERE an.transaction_id = trx.id " +
            "GROUP BY trx.id HAVING count(*) > #{size} ")
    List<LargeTransactionBean> findLargeTransactions(@Param("size") int size);

    /*
     * Access Control List
     */
    @Select("select count(*) count from alf_access_control_list")
    String countTotalAcls();

    @Select("SELECT count(*) count from alf_access_control_list aacl " +
            "LEFT OUTER JOIN alf_node an ON an.acl_id=aacl.id " +
            "WHERE aacl.id IS NULL")
    String countTotalOrphanedAcls();

    @Select("select ap.name permission, count(*) count " +
            "from alf_access_control_entry aace, alf_permission ap " +
            "where ap.id = aace.permission_id " +
            "group by ap.name")
    List<AccessControlBean> findAccessControlListEntries();

    @Select("SELECT acl_id aclId, count(*) count " +
            "FROM alf_node " +
            "GROUP BY acl_id ORDER BY count DESC LIMIT 10")
    List<AccessControlBean> findACLNodeRepartition();

    @Select("SELECT * FROM (SELECT acl_id aclId, count(*) count " +
            "FROM alf_node " +
            "GROUP BY acl_id ORDER BY count DESC) " +
            "WHERE ROWNUM <= 10")
    List<AccessControlBean> findACLNodeRepartitionOracle();

    @Select("SELECT TOP 10 acl_id aclId, count(*) count " +
            "FROM alf_node " +
            "GROUP BY acl_id ORDER BY count DESC")
    List<AccessControlBean> findACLNodeRepartitionMSSql();

    @Select("SELECT md5(aa.authority) AS authorityHash, count(*) AS count " +
            "FROM alf_access_control_entry ace " +
            "JOIN alf_authority aa ON aa.id=ace.authority_id " +
            "GROUP BY authorityHash HAVING count(*) > 0")
    List<AccessControlBean> findACEAuthorities();

    @Select("SELECT standard_hash(aa.authority, 'MD5') AS authorityHash, count(*) AS count " +
            "FROM alf_access_control_entry ace " +
            "JOIN alf_authority aa ON aa.id=ace.authority_id " +
            "GROUP BY aa.authority HAVING count(*) > 0")
    List<AccessControlBean> findACEAuthoritiesOracle();

    @Select("SELECT CONVERT(VARCHAR(32), HashBytes('MD5', aa.authority), 2) AS authorityHash, count(*) AS count "
            +
            "FROM alf_access_control_entry ace " +
            "JOIN alf_authority aa ON aa.id=ace.authority_id " +
            "GROUP BY aa.authority HAVING count(*) > 0")
    List<AccessControlBean> findACEAuthoritiesMSSql();

    @Select("SELECT CASE type WHEN 0 THEN 'OLD' " +
            "WHEN 1 THEN 'DEFINING' " +
            "WHEN 2 THEN 'SHARED' " +
            "WHEN 3 THEN  'FIXED' " +
            "WHEN 4 THEN 'GLOBAL' " +
            "WHEN 5 THEN 'LAYERED' " +
            "ELSE 'UNKNOWN' " +
            "END as aclType, inherits, count(*) as count " +
            "FROM alf_access_control_list " +
            "GROUP BY type, inherits")
    List<AccessControlBean> findAclTypesRepartition();

    @Select("SELECT acm.acl_id as aclId, count(*) as count FROM " +
            "alf_acl_member acm INNER JOIN alf_access_control_list " +
            "aacl ON aacl.id=acm.acl_id AND aacl.type=1 " +
            "GROUP BY acm.acl_id")
    List<AccessControlBean> findAclsHeight();

    /*
     * Content Model
     */
    @Select("select an.uri model, local_name property " +
            "from alf_qname aq, alf_namespace an " +
            "where an.id = aq.ns_id ")
    List<ContentModelBean> listContentModels();

    /*
     * Activities Feed
     */
    @Select("select count(*) as count, TRIM(cast(CAST(post_date AS date) as char)) postDate, site_network as siteNetwork, activity_type as activityType "
            +
            "from alf_activity_feed " +
            "where feed_user_id = post_user_id " +
            "group by TRIM(cast(CAST(post_date AS date) as char)), site_network, activity_type ")
    List<ActivitiesFeedByTypeBean> listActivitiesByActivityType();

    @Select("select count(*) as count, TRIM(cast(CAST(post_date AS date) as char)) postDate, site_network as siteNetwork, feed_user_id as feedUserId "
            +
            "from alf_activity_feed " +
            "where feed_user_id != '@@NULL@@' " +
            "and feed_user_id = post_user_id " +
            "group by TRIM(cast(CAST(post_date AS date) as char)), site_network, feed_user_id ")
    List<ActivitiesFeedByUserBean> listActivitiesByUser();

    @Select("select count(*) as count, TRIM(cast(CAST(post_date AS date) as char)) postDate, site_network as siteNetwork, app_tool as appTool "
            +
            "from alf_activity_feed " +
            "where feed_user_id != '@@NULL@@' " +
            "and feed_user_id = post_user_id " +
            "group by TRIM(cast(CAST(post_date AS date) as char)), site_network, app_tool ")
    List<ActivitiesFeedByApplication> listActivitiesByApplication();

    /*
     * Archived Nodes
     */
    @Select("select count(*) as count from alf_node " +
            "where store_id in (select id from alf_store where protocol = 'archive' and identifier = 'SpacesStore')")
    String countTotalArchivedNodes();

    @Select("select audit_modifier as auditModifier, count(*) as count " +
            "from alf_node " +
            "where store_id in (select id from alf_store where protocol = 'archive' and identifier = 'SpacesStore') " +
            "group by audit_modifier ")
    List<ArchivedNodesBean> listArchivedNodesByUser();

    /*
     * Locked Resources
     */
    @Select("select al.id, al.lock_token as lockToken, start_time as startTime, expiry_time as expiryTime, " +
            "alr1.qname_localname as sharedResource, alr2.qname_localname as exclusiveResource, uri " +
            "from alf_lock al, alf_lock_resource alr1, alf_lock_resource alr2, alf_namespace an " +
            "where alr1.id = al.shared_resource_id " +
            "and alr2.id = al.excl_resource_id " +
            "and an.id = alr1.qname_ns_id ")
    List<LockedResourcesBean> lockedResources();

    /*
     * Users and Groups
     */
    @Select("select count(*) as count from alf_node_properties " +
            "where node_id in (select id from alf_node where type_qname_id in (select id from alf_qname where local_name = 'person')) "
            +
            "and qname_id in (select id from alf_qname where local_name ='userName')")
    String countTotalUsers();

    @Select("select count(*) as count from alf_auth_status where authorized is TRUE")
    String countAuthorizedUsers();

    @Select("select count(*) as count from alf_auth_status where authorized = 1")
    String countAuthorizedUsersOracle();

    @Select("select count(*) as count from alf_node_properties where qname_id in (select id from alf_qname where local_name = 'authorityName')")
    String countGroups();

    /*
     * Jmx Properties
     */
    @Select("SELECT APSVk.string_value as propertyName,APSVv.string_value as propertyValue " +
            "FROM alf_prop_link APL " +
            "JOIN alf_prop_value APVv ON APL.value_prop_id=APVv.id " +
            "JOIN alf_prop_value APVk ON APL.key_prop_id=APVk.id " +
            "JOIN alf_prop_string_value APSVk ON APVk.long_value=APSVk.id " +
            "JOIN alf_prop_string_value APSVv ON APVv.long_value=APSVv.id " +
            "WHERE APL.key_prop_id <> APL.value_prop_id " +
            "AND APL.root_prop_id IN (SELECT prop1_id FROM alf_prop_unique_ctx)")
    List<JmxPropertiesBean> findJmxProperties();

    /*
     * Workflows
     */
    @Select("select count(*) as count, proc_def_id_ as procDefId, name_ as taskName " +
            "FROM ACT_HI_TASKINST " +
            "GROUP BY proc_def_id_, name_")
    List<WorkflowBean> listWorkflowsWithProcessesAndTasks();

    @Select("select count(proc_def_id_) as count, proc_def_id_  as procDefId " +
            "FROM ACT_HI_PROCINST " +
            "WHERE end_time_ is null " +
            "GROUP BY proc_def_id_")
    List<WorkflowBean> listOpenWorkflows();

    @Select("select count(proc_def_id_) as count, proc_def_id_  as procDefId " +
            "FROM ACT_HI_PROCINST " +
            "WHERE end_time_ is not null " +
            "GROUP BY proc_def_id_")
    List<WorkflowBean> listClosedWorkflows();

    @Select("select count(proc_def_id_) as count, proc_def_id_  as procDefId, name_ as taskName " +
            "FROM ACT_HI_TASKINST " +
            "WHERE end_time_ is null " +
            "GROUP BY proc_def_id_, name_")
    List<WorkflowBean> listOpenTasks();

    @Select("select count(proc_def_id_) as count, proc_def_id_  as procDefId, name_ as taskName " +
            "FROM ACT_HI_TASKINST " +
            "WHERE end_time_ is not null " +
            "GROUP BY proc_def_id_, name_")
    List<WorkflowBean> listClosedTasks();

    /*
     * Nodes
     */
    @Select("SELECT stores.protocol, stores.identifier, count(nodes.id) as count, sum(contentUrl.content_size) totalContentSizeBytes " +
    "FROM alf_node nodes " +
    "  inner join alf_store stores on stores.id=nodes.store_id " +
    "  left join alf_node_properties nodes_props on nodes.id = nodes_props.node_id " +
    "  left join alf_content_data content on nodes_props.qname_id in (select id from alf_qname where local_name = 'content') and nodes_props.long_value = content.id " +
    "  left join alf_content_url contentUrl on contentUrl.id = content.content_url_id " +
    "group by stores.protocol,stores.identifier")
    List<NodeStoreBean> listNodesByStore();

    @Select("SELECT mimetype_str mimeType, count(nodes.id) as count, sum(contentUrl.content_size) totalContentSizeBytes " +
    "FROM alf_node nodes " +
    "  left join alf_node_properties nodes_props on nodes.id = nodes_props.node_id " +
    "  left join alf_content_data content on nodes_props.qname_id in (select id from alf_qname where local_name = 'content') and nodes_props.long_value = content.id " +
    "  left join alf_mimetype mime on content.content_mimetype_id = mime.id " +
    "  left join alf_content_url contentUrl on contentUrl.id = content.content_url_id " +
    "Where nodes.store_id in (select id from alf_store where protocol = 'workspace' and identifier = 'SpacesStore') " +
    "group by mimetype_str ")
    List<NodeMimeTypeBean> listActiveNodesByMimetype();

    @Select("SELECT substr(nodes.audit_created,1,7) as creationDate, ns.uri as namespace, names.local_name as propertyName, count(*) as count " +
    "FROM alf_node nodes " +
    "  JOIN alf_qname names ON (nodes.type_qname_id = names.id) " +
    "  JOIN alf_namespace ns ON (names.ns_id = ns.id) " +
    "WHERE nodes.store_id in (select id from alf_store where protocol = 'workspace' and identifier = 'SpacesStore') " +
    "GROUP BY substr(nodes.audit_created,1,7),ns.uri,names.local_name ")
    List<NodeContentTypeMonthBean> listActiveNodesByContentTypeAndMonth();

    @Select("SELECT ns.uri as namespace, names.local_name as propertyname, count(*) as count " +
    "FROM alf_node nodes " +
    "  JOIN alf_qname names ON (nodes.type_qname_id = names.id) " +
    "  JOIN alf_namespace ns ON (names.ns_id = ns.id) " +
    "WHERE nodes.store_id in (select id from alf_store where protocol = 'workspace' and identifier = 'SpacesStore') " +
    "GROUP BY ns.uri,names.local_name ")
    List<NodeContentTypeBean> listActiveNodesByContentType();

}
