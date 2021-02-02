package com.pb.dp.web.controller;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.pb.dp.model.FieldKey;
import com.pb.dp.service.ConfigService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "config")
public class ConfigController {

    private final Logger logger = LoggerFactory.getLogger(ConfigController.class);

    @Autowired
    private ConfigService configService;

    /**
     * This method is used to refresh all the property maps used.
     * URL: http://localhost:8080/cs/config/refresh
     */
    @RequestMapping(value = "/refresh", method = RequestMethod.GET, produces = {"application/json"})
    public @ResponseBody
    String refreshConfigMaps(@RequestParam(required = false) String param) {
        DBObject json = new BasicDBObject();
        try {
            configService.refreshMaps();
            json.put("msg", "All configs refreshed..");
            json.put("ok", 1);
        } catch (Exception e) {
            logger.error("exception caught while refereshing service URL , msg: " + e.getMessage());
            json = new BasicDBObject().append(FieldKey.SK_MSG, "Exception occured while processing.." + e.getMessage()).append(FieldKey.SK_OK, 0);
        }
        return json.toString();
    }


}