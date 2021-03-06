/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.amt.endpoints.buisiness;

import com.amt.common.cache.CurrentLocationEntityCache;
import com.amt.common.cache.CurrentStatusEntityCache;
import com.amt.entities.buisiness.CurrentLocationEntity;
import com.amt.entities.auth.UserEntity;
import com.amt.entities.buisiness.CurrentStatusEntity;
import com.amt.entities.history.HistoricalStatusEntity;
import com.tna.common.AccessError;
import com.tna.common.UserAccessControl;
import com.tna.data.Persistence;
import com.tna.endpoints.AuthorisedEndpoint;
import java.util.Date;
import javax.servlet.annotation.WebServlet;
import org.json.simple.JSONObject;

/**
 *
 * @author tareq
 */
@WebServlet("/vehicle/status/*")
public class CurrentStatusEndpoint extends AuthorisedEndpoint {

    @Override
    public JSONObject doList(String token) throws AccessError {
        UserAccessControl.authOperation(UserEntity.class, token, 3);
        return Persistence.list(CurrentStatusEntity.class);
    }

    @Override
    public JSONObject doCreate(JSONObject json, String token) throws AccessError {
        throw new AccessError(AccessError.ERROR_TYPE.OPERATION_FAILED);
    }

    @Override
    public JSONObject doUpdate(JSONObject json, long resource, String token) throws AccessError {
        UserAccessControl.authOperation(UserEntity.class, token, 3);
        JSONObject query1 = new JSONObject();
        query1.put("vehicleId",resource);
        
        JSONObject query2 = Persistence.readByProperties(CurrentStatusEntity.class,query1);
        query2.put("timeStamp", new Date().toString());
        Persistence.create(HistoricalStatusEntity.class, query2);
        
        return  Persistence.update(CurrentStatusEntity.class,(int)query2.get("id"),json);
    }

    @Override
    public JSONObject doRead(long resource, String token) throws AccessError {
        UserAccessControl.authOperation(UserEntity.class, token, 3);
        JSONObject result = CurrentStatusEntityCache.retreive((long) resource);
        if (result == null) {
            JSONObject obj = new JSONObject();
            obj.put("vehicleId", resource);
            result = Persistence.readByProperties(CurrentStatusEntity.class, obj);
        }
        return result;
    }

    @Override
    public JSONObject doDelete(long resource, String token) throws AccessError {
        throw new AccessError(AccessError.ERROR_TYPE.OPERATION_FAILED);
    }
    
}
