package com.pb.dp.dao;

public interface HealthDocQuery {

   String GET_HEALTH_DOC_LIST  = "select hd.id as id,hd.customerId,hd.healthId,hd.docName,hd.docOwner,hd.docTypeId,mdt.name as docTypeName,hd.docS3Url,hd.docTags," +
           "hd.medicEntityName,hd.doctorName, hd.createdAt as createdAt,hd.updatedAt as updatedAt from health_doc hd (nolock) " +
           "inner join m_docType mdt on mdt.id = hd.docTypeId where hd.customerId = :customerId and hd.isActive = 1";

   String DELETE_HEALTH_DOC = "DELETE FROM health_doc WHERE id = :id";

   String SOFT_DELETE_HEALTH_DOC = "UPDATE health_doc SET isActive = 0, updatedAt = GETDATE(), updatedBy = :customerId WHERE id = :id";

   String GET_SUBSCRIPTION  = "SELECT * FROM hl_subscription " +
           "where healthId = :healthId AND isActive = 1 order by createdAt desc";

}
