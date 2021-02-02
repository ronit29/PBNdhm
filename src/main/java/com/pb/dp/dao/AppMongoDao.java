package com.pb.dp.dao;
///**
// *
// */
//package com.policybazaar.docprimNdhm.common.dao.impl;
//
//import com.mongodb.*;
//import com.mongodb.client.FindIterable;
//import com.mongodb.client.MongoCollection;
//import com.mongodb.client.MongoCursor;
//import com.mongodb.util.JSON;
//import com.policybazaar.coreservice.common.constant.Const;
//import org.bson.Document;
//import org.bson.types.ObjectId;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import javax.annotation.PreDestroy;
//import java.util.*;
//
///**
// * This class has an abstraction of Mongo functionalities for performing of
// * operations.
// *
// * @author ranjeet
// *
// */
//public class AppMongoDao {
//
//	private static final Logger logger = LoggerFactory.getLogger(AppMongoDao.class);
//
//	private MongoClient mongoClient = null;
//
//	private String host;
//	private String dbName;
//	private int portNo;
//	private String userName;
//	private String password;
//	private int connectionsPerHost;
//	private int socketTimeOut;
//	private int connectionTimeOut;
//	private String replicaSetName;
//	private int replicaSetMode; // 0 for standalone and 1 for replicaset mode
//	private int authEnable;// 0 for authNotEnabled and 1 for authEnabled on mongo db
//
//	/**
//	 * @param host
//	 */
//	public void setHost(String host) {
//		this.host = host;
//	}
//
//	public int getConnectionsPerHost() {
//		return connectionsPerHost;
//	}
//
//	public void setConnectionsPerHost(int connectionsPerHost) {
//		this.connectionsPerHost = connectionsPerHost;
//	}
//
//	public int getSocketTimeOut() {
//		return socketTimeOut;
//	}
//
//	public void setSocketTimeOut(int socketTimeOut) {
//		this.socketTimeOut = socketTimeOut;
//	}
//
//	public int getConnectionTimeOut() {
//		return connectionTimeOut;
//	}
//
//	public void setConnectionTimeOut(int connectionTimeOut) {
//		this.connectionTimeOut = connectionTimeOut;
//	}
//
//	public void setReplicaSetMode(int replicaSetMode) {
//		this.replicaSetMode = replicaSetMode;
//	}
//
//	public void setReplicaSetName(String replicaSetName) {
//		this.replicaSetName = replicaSetName;
//	}
//
//	public void setAuthEnable(int authEnable) {
//		this.authEnable = authEnable;
//	}
//	/**
//	 * @param dbName
//	 */
//	public void setDbName(String dbName) {
//		this.dbName = dbName;
//	}
//
//	/**
//	 * @return
//	 */
//	public String getDbName() {
//		return dbName;
//	}
//
//	/**
//	 * @param portNo
//	 *            the portNo to set
//	 */
//	public void setPortNo(int portNo) {
//		this.portNo = portNo;
//	}
//
//	/**
//	 * This method is used to call after invoking of default constructor
//	 */
//	public void init() {
//		try {
//				if (replicaSetMode == 0) {
//
//			MongoClientOptions options = MongoClientOptions.builder()
//					.connectionsPerHost(connectionsPerHost).connectTimeout(connectionTimeOut).socketTimeout(socketTimeOut).build();
//
//
//			String[] hostsArray = host.split(",");
//			List<ServerAddress> serverList = new ArrayList<ServerAddress>();
//			for (int i = 0; i < hostsArray.length; i++) {
//				serverList.add(new ServerAddress(hostsArray[i], portNo));
//			}
//
//				if(authEnable==1)
//					applyMongoCredentials(options, serverList);
//				else
//					mongoClient = new MongoClient(serverList, options);
//			} if (replicaSetMode == 1) {
//				MongoClientOptions options = MongoClientOptions.builder().readPreference(ReadPreference.secondaryPreferred())
//						.requiredReplicaSetName(replicaSetName).connectionsPerHost(connectionsPerHost).connectTimeout(connectionTimeOut)
//						.socketTimeout(socketTimeOut).build();
//				String[] array = host.split(",");
//				List<ServerAddress> serverAddress = new ArrayList<ServerAddress>();
//				for (String str : array) {
//					serverAddress.add(new ServerAddress(str, portNo));
//				}
//
//				if(authEnable==1)
//					applyMongoCredentials(options, serverAddress);
//				else
//					mongoClient = new MongoClient(serverAddress, options);
//			}
//		} catch (Exception e) {
//			logger.debug("caught Exception while getting connection to mongoDB, msg:" + e.getMessage());
//		}
//
//	}
//
//	private void applyMongoCredentials(MongoClientOptions options, List<ServerAddress> serverList) {
//		MongoCredential credential = MongoCredential.createScramSha1Credential(userName, dbName, password.toCharArray());
//		mongoClient = new MongoClient(serverList, Collections.singletonList(credential), options);
//	}
//
//	/*public void init() {
//		MongoClient mongoClient = null;
//		try {
//			MongoClientOptions options = MongoClientOptions.builder().readPreference(ReadPreference.secondaryPreferred()).requiredReplicaSetName("rs1")
//					.connectionsPerHost(connectionsPerHost).connectTimeout(connectionTimeOut).socketTimeout(socketTimeOut).build();
//
//			// for authentication mechanism mongoCR then use following
//			//MongoCredential credential = MongoCredential.createMongoCRCredential(userName, "admin", password.toCharArray());
//
//
//			// if authentication Mechanism is SCRAM-SHA-1 then user following,
//			//by default from mongodb 3.0 and ownward uses SCRAM-SHA-1 authentication mechanism
//			//MongoCredential credential = MongoCredential.createScramSha1Credential(userName, "admin", password.toCharArray());
//			// mongoClient = new MongoClient(new ServerAddress("10.0.8.20"), Arrays.asList(credential));
//
//				String[] hostsArray = host.split(",");
//				List<ServerAddress> serverList = new ArrayList<ServerAddress>();
//				for (int i = 0; i < hostsArray.length; i++) {
//					serverList.add(new ServerAddress(hostsArray[i], portNo));
//				}
//				mongoClient = new MongoClient(serverList, options);
//				//mongoClient = new MongoClient(serverList, Arrays.asList(credential), options);
//
//		} catch (UnknownHostException e) {
//			logger.debug("caught Exception while getting connection to mongoDB, msg:" + e.getMessage());
//		} catch (Exception e) {
//			logger.debug("caught Exception while getting connection to mongoDB, msg:" + e.getMessage());
//		}
//
//	}*/
//
//	/**
//	 * This method is used to get MongoClient and this is singleton to maintain
//	 * mongoDB connection pooling.
//	 *
//	 * @return
//	 */
//	/*
//	 * public MongoClient getMongoClient() { if (mongoClient == null) { try {
//	 * mongoClient = new MongoClient(host, portNo); } catch
//	 * (UnknownHostException e) { // e.printStackTrace(); logger.debug(
//	 * "caught UnknownHostException in method: getMongoClient, while getting connection to mongoDB, msg:"
//	 * + e.getMessage()); } } return mongoClient; }
//	 */
//
//	/**
//	 * This method is used to get {@link DB} object from {@link MongoClient}.
//	 *
//	 * @return
//	 */
//	public DB getDB() {
//		return mongoClient.getDB(dbName);
//	}
//
//	/**
//	 * This method is used to get {@link DBCollection} object from
//	 * {@link MongoClient}.
//	 *
//	 * @return
//	 */
//	public DBCollection getCollection(String collectionName) {
//		return mongoClient.getDB(dbName).getCollection(collectionName);
//	}
//
//	public MongoCollection<DBObject> getDocumentCollection(String collectionName) {
//		return mongoClient.getDatabase(dbName).getCollection(collectionName, DBObject.class);
//	}
//
//	@SuppressWarnings("unchecked")
//	private Document getDocument(DBObject doc)
//	{
//	   if(doc == null) return null;
//	   return new Document(doc.toMap());
//	}
//
//	/**
//	 * This method is used to get List of {@link BasicDBObject} object by
//	 * provided Collection and mongoDB query.
//	 *
//	 * @param collection
//	 * @param query
//	 * @return
//	 */
//	public List<BasicDBObject> getDBObjects(String collection, DBObject query) {
//		MongoCollection<DBObject> dbCollection = getDocumentCollection(collection);
//		FindIterable<DBObject> cursorDocument = dbCollection.find(getDocument(query));
//		List<BasicDBObject> dbObjects = new ArrayList<BasicDBObject>();
//		MongoCursor<DBObject> cursor = cursorDocument.cursor();
//		try {
//			if (cursor != null) {
//				while (cursor.hasNext()) {
//					BasicDBObject actorObj = (BasicDBObject) cursor.next();
//					dbObjects.add(actorObj);
//				}
//			}
//		} catch (Exception e) {
//			logger.error(e.getMessage());
//			throw e;
//		} finally {
//			if (cursor != null) {
//				cursor.close();
//			}
//		}
//		return dbObjects;
//	}
//
//	/**
//	 * This method is used to get List of {@link BasicDBObject} object by
//	 * provided Collection and mongoDB query.
//	 *
//	 * @param collection
//	 * @param query
//	 * @return
//	 */
//	public List<BasicDBObject> getDBObjects(String collection, DBObject query, DBObject fieldsToReturn) {
//		DB db = mongoClient.getDB(dbName);
//		DBCollection dbCollection = db.getCollection(collection);
//		DBCursor cursor = dbCollection.find(query, fieldsToReturn);
//		List<BasicDBObject> dbObjects = new ArrayList<BasicDBObject>();
//		try {
//			if (cursor != null) {
//				while (cursor.hasNext()) {
//					BasicDBObject actorObj = (BasicDBObject) cursor.next();
//					dbObjects.add(actorObj);
//				}
//			}
//		} catch (Exception e) {
//			logger.error(e.getMessage());
//			throw e;
//		} finally {
//			if (cursor != null) {
//				cursor.close();
//			}
//		}
//		return dbObjects;
//	}
//
//	public List<BasicDBObject> getDBObjects(String collection, DBObject query, DBObject fieldsToReturn, int offset, int length) {
//		DB db = mongoClient.getDB(dbName);
//
//		DBCollection dbCollection = db.getCollection(collection);
//		DBCursor cursor = null;
//		if (fieldsToReturn != null) {
//			cursor = dbCollection.find(query, fieldsToReturn);
//		} else {
//			cursor = dbCollection.find(query);
//		}
//		if (length > 0) {
//			cursor.skip(offset).limit(length);
//		}
//		List<BasicDBObject> dbObjects = new ArrayList<BasicDBObject>();
//		try {
//			if (cursor != null) {
//				while (cursor.hasNext()) {
//					BasicDBObject actorObj = (BasicDBObject) cursor.next();
//					dbObjects.add(actorObj);
//				}
//			}
//		} catch (Exception e) {
//			logger.error(e.getMessage());
//			throw e;
//		} finally {
//			if (cursor != null) {
//				cursor.close();
//			}
//		}
//		return dbObjects;
//	}
//
//	public List<BasicDBObject> getDBObjects(String collection, DBObject query, int offset, int limit) {
//		DB db = mongoClient.getDB(dbName);
//
//		DBCollection dbCollection = db.getCollection(collection);
//		DBCursor cursor = dbCollection.find(query);
//		if (limit > 0) {
//			cursor.skip(offset).limit(limit);
//		}
//		List<BasicDBObject> dbObjects = new ArrayList<BasicDBObject>();
//		try {
//			if (cursor != null) {
//				while (cursor.hasNext()) {
//					BasicDBObject actorObj = (BasicDBObject) cursor.next();
//					dbObjects.add(actorObj);
//				}
//			}
//		} catch (Exception e) {
//			logger.error(e.getMessage());
//			throw e;
//		} finally {
//			if (cursor != null) {
//				cursor.close();
//			}
//		}
//		return dbObjects;
//	}
//
//	/**
//	 * This method takes sortBy as one of the integer parameter to specify
//	 * whether to sort in ascending or descending order
//	 *
//	 * @param collection
//	 * @param query
//	 * @param fieldsToReturn
//	 * @param offset
//	 * @param length
//	 * @param orderBy
//	 * @return
//	 */
//	public List<BasicDBObject> getDBObjects(String collection, DBObject query, DBObject fieldsToReturn, int offset, int length, DBObject orderBy) {
//		DB db = mongoClient.getDB(dbName);
//		// Tells the nature of sorting on C_AT
//		/*DBObject orderBy = new BasicDBObject();
//		orderBy.put("C_AT", sortBy);*/
//		if (collection == null || query == null) {
//			throw new IllegalArgumentException("collectionName and query can't be null");
//		}
//		DBCollection dbCollection = db.getCollection(collection);
//
//		DBCursor cursor = null;
//		if (fieldsToReturn != null) {
//			cursor = dbCollection.find(query, fieldsToReturn);
//		} else {
//			cursor = dbCollection.find(query); // cursor = dbCollection.find(query).sort(orderBy);
//		}
//
//		if (orderBy != null) {
//			cursor.sort(orderBy);
//		}
//
//		if (length > 0) {
//			cursor.skip(offset).limit(length);
//
//		}
//
//		List<BasicDBObject> dbObjects = new ArrayList<BasicDBObject>();
//		try {
//			if (cursor != null) {
//				while (cursor.hasNext()) {
//					//BasicDBObject actorObj = (BasicDBObject) cursor.next();
//					dbObjects.add((BasicDBObject) cursor.next());
//				}
//			}
//		} catch (Exception e) {
//			logger.error(e.getMessage());
//			throw e;
//		} finally {
//			if (cursor != null) {
//				cursor.close();
//			}
//		}
//		return dbObjects;
//	}
//
//	public int getCursorCount(String collection, DBObject query) {
//
//		DB db = mongoClient.getDB(dbName);
//		DBCursor cursor = null;
//		int count = 0;
//		try {
//			DBCollection dbCollection = db.getCollection(collection);
//			cursor = dbCollection.find(query);
//			count = cursor.count();
//		} catch (Exception e) {
//			logger.error(e.getMessage());
//			throw e;
//		} finally {
//			if (cursor != null) {
//				cursor.close();
//			}
//		}
//		return count;
//	}
//
//	public List<BasicDBObject> getDBObjects(String collection) {
//		DB db = mongoClient.getDB(dbName);
//		DBCollection dbCollection = db.getCollection(collection);
//		DBCursor cursor = dbCollection.find();
//		List<BasicDBObject> dbObjects = new ArrayList<BasicDBObject>();
//		try {
//			if (cursor != null) {
//				while (cursor.hasNext()) {
//					BasicDBObject actorObj = (BasicDBObject) cursor.next();
//					dbObjects.add(actorObj);
//				}
//			}
//		} catch (Exception e) {
//			logger.error(e.getMessage());
//			throw e;
//		} finally {
//			if (cursor != null) {
//				cursor.close();
//			}
//		}
//		return dbObjects;
//	}
//
//	/**
//	 * This method is used to get {@link DBObject} object by provided Collection
//	 * and mongoDB query.
//	 *
//	 * @param collection
//	 * @param query
//	 * @return
//	 */
//	public DBObject getDBObject(String collection, DBObject query) {
//		DB db = mongoClient.getDB(dbName);
//
//		DBCollection dbCollection = db.getCollection(collection);
//		DBObject dbObject = (DBObject) dbCollection.findOne(query);
//		return dbObject;
//	}
//
//	public DBObject getDBObjectFromPrimary(String collection, DBObject query) {
//		mongoClient.setReadPreference(ReadPreference.primaryPreferred());
//		DB db = mongoClient.getDB(dbName);
//		DBCollection dbCollection = db.getCollection(collection);
//		DBObject dbObject = (DBObject) dbCollection.findOne(query);
//		return dbObject;
//	}
//
//	/*
//	 * public List<DBObject> getDBObjects(String collection, DBObject query) {
//	 * DB db = mongoClient.getDB( DB_NAME );
//	 *
//	 * DBCollection dbCollection = db.getCollection(collection); List<DBObject>
//	 * dbObject = dbCollection.(query); return dbObject; }
//	 */
//
//	/**
//	 * This method is used to call before shutting down of vm.
//	 */
//	@PreDestroy
//	public void destroy() {
//		try {
//			if (mongoClient != null) {
//				mongoClient.close();
//				Thread.sleep(20000);
//			}
//		} catch (InterruptedException e) {
//			logger.error("Exception in sleeping thread before releaseing mongoConnection mongo., msg : " + e.getMessage(), e);
//		} finally {
//
//		}
//
//	}
//
//	/**
//	 * @param str
//	 * @return
//	 */
//	private DBObject getDBObjectsFromString(String str) {
//		DBObject dbObject = (DBObject) JSON.parse(str);
//		return dbObject;
//	}
//
//	/**
//	 * This method is used to add {@link DBObject} object to provided
//	 * collection.
//	 *
//	 * @param collectionName
//	 * @param rowJson
//	 * @throws Exception
//	 */
//	public void addRow(String collectionName, DBObject rowJson) throws Exception {
//		try {
//			MongoCollection<DBObject> dbColl = getDocumentCollection(collectionName);
//			dbColl.insertOne(rowJson);
//		} catch (MongoException e) {
//			logger.error("Problem in addtion not added Mongo collection=" + collectionName + " rowJson =" + rowJson, e);
//			throw e;
//		} catch (Exception e) {
//			logger.error("document not added Exception, msg : ", e);
//			throw e;
//		} finally {
//
//		}
//	}
//
//	/**
//	 * This method is used to add valid raw JSON string object to provided
//	 * collection.
//	 *
//	 * @param collectionName
//	 * @param rowJson
//	 * @return
//	 * @throws Exception
//	 */
//	public String addRow(String collectionName, String rowJson) throws Exception {
//		DB mongoDB = null;
//		try {
//			mongoDB = mongoClient.getDB(dbName);
//			// mongoDB.requestStart();
//			DBCollection dbColl = mongoDB.getCollection(collectionName);
//			WriteResult res = dbColl.insert(getDBObjectsFromString(rowJson), WriteConcern.MAJORITY);
//			/*
//			 * CommandResult resErr =
//			 * mongoDB.getLastError(res.getLastConcern()); if (res.getError() !=
//			 * null) { throw new MongoException(resErr.getErrorMessage()); }
//			 */
//			return rowJson;
//		} catch (MongoException e) {
//			logger.error("Problem in addtion not added Mongo collection=" + collectionName + " rowJson =" + rowJson, e);
//			throw e;
//		} catch (Exception e) {
//			logger.error("User not added Exception", e);
//			throw e;
//		} finally {
//			/*
//			 * if (mongoDB != null) { try { mongoDB.resetError();
//			 * mongoDB.requestDone(); } catch (Exception e) {
//			 * logger.error("Exception while resetting DBConnections", e); } }
//			 */
//		}
//	}
//
//	/**
//	 * This method is used to update rawJson as DBObject to provided collection
//	 * based on query i.e. <code>DBOject</code>.
//	 *
//	 * @param collectionName
//	 * @param queryJson
//	 * @param rowJson
//	 * @param upsert
//	 * @param multi
//	 * @throws Exception
//	 */
//	public void updateRow(String collectionName, DBObject queryJson, DBObject rowJson, boolean upsert, boolean multi) throws Exception {
//		DB mongoDB = null;
//		WriteConcern concern = WriteConcern.SAFE;
//		try {
//
//			mongoDB = mongoClient.getDB(dbName);
//			//mongoDB.requestStart();
//			DBCollection userColl = mongoDB.getCollection(collectionName);
//			// DBObject dbObject = new BasicDBObject();
//			WriteResult res = userColl.update(queryJson, rowJson, upsert, multi, concern);
//			/*
//			 * CommandResult resErr =
//			 * mongoDB.getLastError(res.getLastConcern()); if (res.getError() !=
//			 * null) { throw new MongoException(resErr.getErrorMessage()); }
//			 */
//		} catch (MongoException e) {
//			logger.error("exception occured while invoking updateRow Mongo into mongo collectionName:" + collectionName + " , msg:" + e.getMessage(), e);
//			throw e;
//		} catch (Exception e) {
//			logger.error("exception occured while invoking updateRow into mongo collectionName:" + collectionName + " , msg:" + e.getMessage(), e);
//			throw e;
//		} finally {
//
//		}
//	}
//
//	/**
//	 * This method is used to update valid rawJson as string to provided
//	 * collection based on query i.e. <code>DBOject</code>.
//	 *
//	 * @param collectionName
//	 * @param queryJson
//	 * @param rowJson
//	 * @throws Exception
//	 */
//	public void updateRow(String collectionName, String queryJson, String rowJson) throws Exception {
//		DB mongoDB = null;
//		WriteConcern concern = WriteConcern.SAFE;
//		boolean multi = false;
//		try {
//			if (rowJson.contains("$")) {
//				multi = true;
//			}
//			mongoDB = mongoClient.getDB(dbName);
//			//mongoDB.requestStart();
//			DBCollection userColl = mongoDB.getCollection(collectionName);
//			// DBObject dbObject = new BasicDBObject();
//			WriteResult res = userColl.update(getDBObjectsFromString(queryJson), getDBObjectsFromString(rowJson), true, multi, concern);
//			/*
//			 * CommandResult resErr =
//			 * mongoDB.getLastError(res.getLastConcern()); if (res.getError() !=
//			 * null) { throw new MongoException(resErr.getErrorMessage()); }
//			 */
//		} catch (MongoException e) {
//			logger.error("exception occured while invoking updateRow Mongo into mongo collectionName:" + collectionName + " , msg:" + e.getMessage(), e);
//			throw e;
//		} catch (Exception e) {
//			logger.error("exception occured while invoking updateRow into mongo collectionName:" + collectionName + " , msg:" + e.getMessage(), e);
//			throw e;
//		} finally {
//			/*
//			 * if (mongoDB != null) { try { mongoDB.resetError();
//			 * mongoDB.requestDone(); } catch (Exception e) {
//			 * logger.error("Exception while resetting DBConnections", e); } }
//			 */
//		}
//	}
//
//	/**
//	 * This method is used to update rawJson as DBObject to provided collection
//	 * based on query i.e. <code>DBOject</code>.
//	 *
//	 * @param collectionName
//	 * @param queryJson
//	 * @param rowJson
//	 * @param upsert
//	 * @param multi
//	 * @throws Exception
//	 */
//	public DBObject upsertRow(String collectionName, DBObject queryJson, DBObject rowJson, boolean upsert, boolean multi) throws Exception {
//		DB mongoDB = null;
//		try {
//			DBObject query = new BasicDBObject();
//			mongoDB = mongoClient.getDB(dbName);
//			DBCollection userColl = mongoDB.getCollection(collectionName);
//			WriteResult result = userColl.update(queryJson, rowJson, upsert, multi, WriteConcern.MAJORITY);
//			if (result.isUpdateOfExisting())
//				query.put("_id", queryJson.get("_id"));
//			else
//				query.put("_id", new ObjectId(result.getUpsertedId().toString()));
//			return userColl.findOne(query);
//
//		} catch (MongoException e) {
//			logger.error("exception occured while invoking updateRow Mongo into mongo collectionName:" + collectionName + " , msg:" + e.getMessage(), e);
//			throw e;
//		} catch (Exception e) {
//			logger.error("exception occured while invoking updateRow into mongo collectionName:" + collectionName + " , msg:" + e.getMessage(), e);
//			throw e;
//		} finally {
//
//		}
//	}
//
//	/**
//	 * This method is used to find and modify based on query parameters to the
//	 * provided collections.
//	 *
//	 * @param collectionName
//	 * @param query
//	 * @param json
//	 * @return
//	 * @throws Exception
//	 */
//	public DBObject findAndModifyRow(String collectionName, DBObject query, DBObject json) throws Exception {
//		DB mongoDB = null;
//		try {
//			mongoDB = mongoClient.getDB(dbName);
//			// mongoDB.requestStart();
//			DBCollection userColl = mongoDB.getCollection(collectionName);
//			DBObject dbObject = userColl.findAndModify(query, json);
//			return dbObject;
//		} catch (MongoException e) {
//			logger.error("DBObject not find and Modify Mongo into mongo collectionName:" + collectionName + " , msg:" + e.getMessage(), e);
//			throw e;
//		} catch (Exception e) {
//			logger.error("DBObject not findAndModify Mongo into mongo collectionName:" + collectionName + " , msg:" + e.getMessage(), e);
//			throw e;
//		} finally {
//
//		}
//	}
//
//	public DBObject findAndModifyOrAddRow(String collectionName, DBObject query, DBObject updateOrAdd) {
//		DB mongoDB = null;
//		try {
//			mongoDB = mongoClient.getDB(dbName);
//			DBCollection userColl = mongoDB.getCollection(collectionName);
//			DBObject dbObject = userColl.findAndModify(query, null, null, false, updateOrAdd, true, true);
//			return dbObject;
//		} catch (MongoException e) {
//			logger.error("Unable to update/add DBObject in collectionName:" + collectionName + " , msg:" + e.getMessage(), e);
//			throw e;
//		} catch (Exception e) {
//			logger.error("Unable to update/add DBObject in collectionName:" + collectionName + " , msg:" + e.getMessage(), e);
//			throw e;
//		}
//	}
//
//	/**
//	 * This method is used to find and modify based on query parameters to the
//	 * provided collections.
//	 *
//	 * @param collectionName
//	 * @param queryJson
//	 * @param rowJson
//	 * @return
//	 * @throws Exception
//	 */
//	public DBObject updateAndFindRow(String collectionName, String queryJson, String rowJson) throws Exception {
//		DB mongoDB = null;
//		try {
//			mongoDB = mongoClient.getDB(dbName);
//			// mongoDB.requestStart();
//			DBCollection userColl = mongoDB.getCollection(collectionName);
//			DBObject dbObject = userColl.findAndModify(getDBObjectsFromString(queryJson), getDBObjectsFromString(rowJson));
//			return dbObject;
//		} catch (MongoException e) {
//			logger.error("User not added Mongo", e);
//			throw e;
//		} catch (Exception e) {
//			logger.error("User not added Exception", e);
//			throw e;
//		} finally {
//			/*
//			 * if (mongoDB != null) { try { mongoDB.resetError();
//			 * mongoDB.requestDone(); } catch (Exception e) {
//			 * logger.error("Exception while resetting DBConnections", e); } }
//			 */
//		}
//	}
//
//	public void deleteRow(String collectionName, String queryJson) throws Exception {
//		DB mongoDB = null;
//		try {
//			mongoDB = mongoClient.getDB(dbName);
//			// mongoDB.requestStart();
//			DBCollection userColl = mongoDB.getCollection(collectionName);
//			WriteResult res = userColl.remove(getDBObjectsFromString(queryJson));
//			/*
//			 * CommandResult resErr =
//			 * mongoDB.getLastError(res.getLastConcern()); if (res.getError() !=
//			 * null) { throw new MongoException(resErr.getErrorMessage()); }
//			 */
//		} catch (MongoException e) {
//			logger.error("document not added in Mongo, msg :" + e.getMessage(), e);
//			throw e;
//		} catch (Exception e) {
//			logger.error("document not added Exception , msg :" + e.getMessage(), e);
//			throw e;
//		} finally {
//			/*
//			 * if (mongoDB != null) { try { mongoDB.resetError();
//			 * mongoDB.requestDone(); } catch (Exception e) {
//			 * logger.error("Exception while resetting DBConnections", e); } }
//			 */
//		}
//	}
//
//	public List<DBObject> findResults(String collectionName, String queryJson, Map<String, Object> options) {
//		DB mongoDB = null;
//		List<DBObject> objectList = new ArrayList<DBObject>();
//		DBCursor cursor = null;
//		try {
//			mongoDB = mongoClient.getDB(dbName);
//			DBCollection collection = mongoDB.getCollection(collectionName);
//			Set<String> keys = options.keySet();
//			if (keys.contains("key")) {
//				cursor = collection.find(getDBObjectsFromString(queryJson), getDBObjectsFromString((String) options.get("key")));
//			} else {
//				cursor = collection.find(getDBObjectsFromString(queryJson));
//			}
//
//			cursor = modifyDBCursor(cursor, options);
//
//			while (cursor.hasNext()) {
//				DBObject actorObj = cursor.next();
//				objectList.add(actorObj);
//			}
//			return objectList;
//		} finally {
//			if (cursor != null) {
//				cursor.close();
//			}
//		}
//	}
//
//	private DBCursor modifyDBCursor(DBCursor cursor, Map<String, Object> options) {
//		if (options != null) {
//			Set<String> keys = options.keySet();
//			try {
//				if (keys.contains("sort")) {
//					cursor = cursor.sort(getDBObjectsFromString((String) options.get("sort")));
//				}
//
//				if (keys.contains("skip")) {
//					cursor = cursor.skip((Integer) options.get("skip"));
//				}
//				if (keys.contains("limit")) {
//					cursor = cursor.limit((Integer) options.get("limit"));
//				}
//			} catch (Exception e) {
//				logger.error(e.getMessage());
//				throw e;
//			} finally {
//				if(cursor != null)
//				cursor.close();
//			}
//		}
//		return cursor;
//	}
//
//	/*
//	 * public String group(String collectioName, String query, String key,
//	 * String initial, String reduce) { DB mongoDB = slaveMongo.getDB(DB);
//	 * DBCollection collection = mongoDB.getCollection(collectioName); DBObject
//	 * dbObject = collection.group(getDBObjectsFromString(key),
//	 * getDBObjectsFromString(query), getDBObjectsFromString(initial), reduce);
//	 * if(dbObject != null){ return dbObject.toString(); } return null; }
//	 */
//
//	/**
//	 * This method is used to add the overloaded method with dBObject.
//	 *
//	 * @param collectionName
//	 * @param rowJson
//	 * @throws Exception
//	 */
//	public DBObject insertRow(String collectionName, DBObject rowJson) throws Exception {
//		DB mongoDB = null;
//		// WriteConcern concern = WriteConcern.SAFE;
//		try {
//			mongoDB = mongoClient.getDB(dbName);
//			DBCollection userColl = mongoDB.getCollection(collectionName);
//			WriteResult res = userColl.insert(rowJson, WriteConcern.MAJORITY);
//			/*
//			 * CommandResult resErr =
//			 * mongoDB.getLastError(res.getLastConcern()); if (res.getError() !=
//			 * null) { throw new MongoException(resErr.getErrorMessage()); }
//			 */
//
//			return rowJson;
//		} catch (MongoException e) {
//			logger.error("Problem in addtion not added Mongo collection=" + collectionName + " rowJson =" + rowJson, e);
//			throw e;
//		} catch (Exception e) {
//			logger.error(" exception caught while adding documents msg:" + e.getMessage(), e);
//			throw e;
//		} finally {
//
//		}
//
//	}
//
//	public boolean insertRows(String collectionName, List<DBObject> rowJsonObjects) {
//		DB mongoDB;
//		try {
//			mongoDB = mongoClient.getDB(dbName);
//			DBCollection userColl = mongoDB.getCollection(collectionName);
//			BulkWriteOperation builder = userColl.initializeUnorderedBulkOperation();
//			rowJsonObjects.forEach(builder::insert);
//			BulkWriteResult result = builder.execute();
//			return result.isAcknowledged();
//		} catch (Exception e) {
//			logger.error(" exception caught while adding multiple documents msg:" + e.getMessage(), e);
//			throw e;
//		}
//	}
//
//	/**
//	 * This method is used to add document/row and returns _id as string.
//	 *
//	 * @param collectionName
//	 * @param rowJson
//	 * @return mongodb document id
//	 * @throws Exception
//	 */
//	public String addRowAndGetId(String collectionName, DBObject rowJson) throws Exception {
//		DB mongoDB = null;
//		String id = null;
//		try {
//			mongoDB = mongoClient.getDB(dbName);
//			DBCollection userColl = mongoDB.getCollection(collectionName);
//			ObjectId _id = new ObjectId();
//			rowJson.put("_id", _id);
//			WriteResult res = userColl.insert(rowJson, WriteConcern.MAJORITY);
//			if (res != null) {
//				id = _id.toString();
//			}
//		} catch (MongoException e) {
//			logger.error("Problem in addtion not added Mongo collection=" + collectionName + " rowJson =" + rowJson, e);
//			throw e;
//		} catch (Exception e) {
//			logger.error(" exception caught while adding documents msg:" + e.getMessage(), e);
//			throw e;
//		} finally {
//
//		}
//		return id;
//	}
//
//	public String addRowAndGetIdPrimary(String collectionName, DBObject rowJson) throws Exception {
//		DB mongoDB = null;
//		String id = null;
//		try {
//			mongoDB = mongoClient.getDB(dbName);
//			DBCollection userColl = mongoDB.getCollection(collectionName);
//			ObjectId _id = new ObjectId();
//			rowJson.put("_id", _id);
//			WriteResult res = userColl.insert(rowJson);
//			if (res != null) {
//				id = _id.toString();
//			}
//		} catch (MongoException e) {
//			logger.error("Problem in addtion not added Mongo collection=" + collectionName + " rowJson =" + rowJson, e);
//			throw e;
//		} catch (Exception e) {
//			logger.error(" exception caught while adding documents msg:" + e.getMessage(), e);
//			throw e;
//		} finally {
//
//		}
//		return id;
//	}
//
//	/**
//	 * @param type
//	 * @return
//	 */
//	public long getSequenceByType(String type) {
//		long nextSequence = 0;
//		try {
//			DB db = mongoClient.getDB(dbName);
//			DBCollection dbCollection = db.getCollection(Const.MongoRepo.COLL_APP_SEQUENCE);
//			DBObject query = new BasicDBObject("type", type);
//
//			// create an increment query
//			DBObject modifier = new BasicDBObject("nextVal", 1);
//			DBObject incQuery = new BasicDBObject("$inc", modifier);
//
//			dbCollection.update(query, incQuery, true, false);
//
//			BasicDBObject dbObjects = (BasicDBObject) dbCollection.findOne(query);
//			nextSequence = dbObjects.getLong("nextVal");
//
//		} catch (Exception e) {
//			// e.printStackTrace();
//			logger.error("exception in gettig nextSequence , collectionName : " + Const.MongoRepo.COLL_APP_SEQUENCE + " type: " + type + " ,msg :" + e.getMessage());
//		}
//		return nextSequence;
//	}
//
//	/**
//	 * @return the userName
//	 */
//	public String getUserName() {
//		return userName;
//	}
//
//	/**
//	 * @param userName
//	 *            the userName to set
//	 */
//	public void setUserName(String userName) {
//		this.userName = userName;
//	}
//
//	/**
//	 * @return the password
//	 */
//	public String getPassword() {
//		return password;
//	}
//
//	/**
//	 * @param password
//	 *            the password to set
//	 */
//	public void setPassword(String password) {
//		this.password = password;
//	}
//
//	/**
//	 * TODO: This method is used for testing purpose only will remove this later
//	 * point of time.
//	 *
//	 * @return
//	 */
//	/*
//	 * public static DBObject getDBObjectByQuery(String collenctionName,
//	 * DBObject query) { DBObject jsonData = null; Mongo mongo = null; DB db =
//	 * null; try { mongo = new Mongo("10.0.11.55", 27017); db =
//	 * mongo.getDB("pocexcel"); // get a single collection DBCollection
//	 * collection = db.getCollection("jsonMaster");
//	 *
//	 * jsonData = collection.findOne(query);
//	 *
//	 * } catch (Exception e) { e.printStackTrace(); } finally { mongo.close();
//	 *
//	 * }
//	 *
//	 * return jsonData; }
//	 */
//
//	public int getUniqueValues(String collection, List<DBObject> funcList) {
//		int count = 0;
//		DB db = mongoClient.getDB(dbName);
//		DBCollection dbCollection = db.getCollection(collection);
//		AggregationOutput output = dbCollection.aggregate(funcList);
//		for (DBObject obj : output.results()) {
//			count++;
//		}
//		return count;
//	}
//
//	public Object getRepeatValues(String collection, List<DBObject> funcList) {
//		int count = 0;
//		DB db = mongoClient.getDB(dbName);
//		DBCollection dbCollection = db.getCollection(collection);
//		AggregationOutput output = dbCollection.aggregate(funcList);
//		for (DBObject obj : output.results()) {
//			count++;
//		}
//		return count;
//	}
//
//	/**
//	 * Will return all documents from collections sorting by object given in <b>orderBy</b> or else if not given
//	 * then sort by "_id" in order given in <b>sortIn</b>
//	 *
//	 * @param collection
//	 * @param sortIn
//	 * @param orderBy
//	 * @return
//	 */
//	@SuppressWarnings("unchecked")
//	public List<BasicDBObject> getDBObjects(String collection, int sortIn, DBObject orderBy) {
//		DB db = mongoClient.getDB(dbName);
//		List<BasicDBObject> dbObjects = new ArrayList<BasicDBObject>();
//		DBCollection dbCollection = db.getCollection(collection);
//		if(orderBy != null) {
//			orderBy.put("_id ", sortIn);
//		}
//		DBCursor cursor = dbCollection.find().sort(orderBy);
//		if (cursor != null) {
//			while (cursor.hasNext()) {
//				BasicDBObject actorObj = (BasicDBObject) cursor.next();
//				dbObjects.add(actorObj);
//			}
//		}
//		return dbObjects;
//	}
//
//
//}
