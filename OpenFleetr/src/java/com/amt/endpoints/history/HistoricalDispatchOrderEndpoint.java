/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.amt.endpoints.history;

import com.amt.entities.history.HistoricalDispatchOrderEntity;
import com.amt.entities.auth.UserEntity;
import com.tna.common.AccessError;
import com.tna.common.UserAccessControl;
import com.tna.data.Persistence;
import com.tna.endpoints.AuthorisedEndpoint;
import javax.servlet.annotation.WebServlet;
import org.json.simple.JSONObject;

/**
 *
 * @author tareq
 */
@WebServlet("/vehicle/dispatch/history/*")
public class HistoricalDispatchOrderEndpoint extends AuthorisedEndpoint{

    @Override
    public JSONObject doList(String token) throws AccessError {
        UserAccessControl.authOperation(UserEntity.class, token, 3);
        return Persistence.list(HistoricalDispatchOrderEntity.class);
    }

    @Override
    public JSONObject doCreate(JSONObject json, String token) throws AccessError {
       throw new AccessError(AccessError.ERROR_TYPE.OPERATION_FAILED);
    }

    @Override
    public JSONObject doUpdate(JSONObject json, long resource, String token) throws AccessError {
        throw new AccessError(AccessError.ERROR_TYPE.OPERATION_FAILED);
    }
    
    @Override
    public JSONObject doRead(long resource, String token) throws AccessError {
       UserAccessControl.authOperation(UserEntity.class, token, 3);
       JSONObject query = new JSONObject();
       query.put("vehicleId", resource);
       return Persistence.readByProperties(HistoricalDispatchOrderEntity.class,query);
    }

    @Override
    public JSONObject doDelete(long resource, String token) throws AccessError {
        throw new AccessError(AccessError.ERROR_TYPE.OPERATION_FAILED);
    }
    
}
