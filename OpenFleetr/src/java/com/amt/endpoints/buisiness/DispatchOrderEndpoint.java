/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.amt.endpoints.buisiness;

import com.amt.common.cache.CurrentDispatchOrderEntityCache;
import com.amt.common.cache.CurrentStatusEntityCache;
import com.amt.entities.buisiness.CurrentLocationEntity;
import com.amt.entities.buisiness.CurrentStatusEntity;
import com.amt.entities.buisiness.CurrentDispatchOrderEntity;
import com.amt.entities.management.DriverEntity;
import com.amt.entities.history.HistoricalDispatchOrderEntity;
import com.amt.entities.history.HistoricalLocationEntity;
import com.amt.entities.auth.UserEntity;
import com.amt.entities.buisiness.CaseEntity;
import com.amt.entities.management.DispatcherEntity;
import com.amt.entities.management.JurisdictionEntity;
import com.tna.common.AccessError;
import com.tna.common.AccessError.ERROR_TYPE;
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
@WebServlet("/vehicle/dispatch/*")
public class DispatchOrderEndpoint extends AuthorisedEndpoint {

    @Override
    public JSONObject doList(String token) throws AccessError {
        UserAccessControl.authOperation(UserEntity.class, token, 3);
        return Persistence.list(CurrentDispatchOrderEntity.class);
    }

    @Override
    public JSONObject doCreate(JSONObject json, String token) throws AccessError {
        UserAccessControl.authOperation(UserEntity.class, token, 3);
        JSONObject user = UserAccessControl.fetchUserByToken(UserEntity.class, token);

        JSONObject query1 = new JSONObject();
        query1.put("userId", user.get("id"));
        JSONObject driver = Persistence.readByProperties(DriverEntity.class, query1);

        JSONObject query2 = new JSONObject();
        query2.put("driverId", driver.get("id"));
        JSONObject dispatchOrder = Persistence.readByProperties(CurrentDispatchOrderEntity.class, query2);

        if (dispatchOrder == null) {
            throw new AccessError(ERROR_TYPE.USER_NOT_ALLOWED);
        }
        dispatchOrder.put("completionDate", new Date().toString());
        Persistence.create(HistoricalDispatchOrderEntity.class, dispatchOrder);
        return Persistence.delete(CurrentDispatchOrderEntity.class, (long) dispatchOrder.get("id"));

    }

    @Override
    public JSONObject doUpdate(JSONObject json, long resource, String token) throws AccessError {
        UserAccessControl.authOperation(UserEntity.class, token, 3);

        JSONObject query1 = new JSONObject();
        query1.put("userId", UserAccessControl.fetchUserByToken(UserEntity.class, token).get("id"));
        long l = (long) Persistence.readByProperties(DispatcherEntity.class, query1).get("id");

        JSONObject query2 = new JSONObject();
        query2.put("dispatcherId", l);
        JSONObject jurisdictions = Persistence.listByProperties(JurisdictionEntity.class, query2);

        JSONObject query3 = new JSONObject();
        query3.put("vehicleId", resource);
        JSONObject readVehicleStatus = Persistence.readByProperties(CurrentStatusEntity.class, query3);

        if (readVehicleStatus != null && readVehicleStatus.containsKey("status")) {
            if ((long) readVehicleStatus.get("status") != 2) {
                throw new AccessError(ERROR_TYPE.ENTITY_UNAVAILABLE);
            } else {
                JSONObject readVehicleLocation = Persistence.readByProperties(CurrentLocationEntity.class, query3);
                if (readVehicleLocation == null) {
                    throw new AccessError(ERROR_TYPE.ENTITY_NOT_FOUND);
                } else {
                    for (Object key : jurisdictions.keySet()) {
                        JSONObject jurisdiction = (JSONObject) jurisdictions.get(key);
                        if ((long)readVehicleLocation.get("geographicalAreaId") == (long)jurisdiction.get("geographicalAreaId")) {

                            json.put("startLatitude", readVehicleLocation.get("latitude"));
                            json.put("startLongitude", readVehicleLocation.get("longitude"));
                            json.put("creationDate", new Date().toString());
                            json.put("caseId", Persistence.create(CaseEntity.class, json).get("key"));
                            json.put("status", 1);

                            return Persistence.create(CurrentDispatchOrderEntity.class, json);
                        }
                    }
                    throw new AccessError(ERROR_TYPE.USER_NOT_ALLOWED);
                }
            }
        } else {
            throw new AccessError(ERROR_TYPE.ENTITY_NOT_FOUND);
        }
    }

    @Override
    public JSONObject doRead(long resource, String token) throws AccessError {
        UserAccessControl.authOperation(UserEntity.class, token, 3);
        JSONObject result = CurrentDispatchOrderEntityCache.retreive((long) resource);
        if (result == null) {
            JSONObject obj = new JSONObject();
            obj.put("vehicleId", resource);
            result = Persistence.readByProperties(CurrentDispatchOrderEntity.class, obj);
        }
        return result;
    }

    @Override
    public JSONObject doDelete(long resource, String token) throws AccessError {
        UserAccessControl.authOperation(UserEntity.class, token, 3);
        return Persistence.delete(CurrentDispatchOrderEntity.class, resource);
    }

}
