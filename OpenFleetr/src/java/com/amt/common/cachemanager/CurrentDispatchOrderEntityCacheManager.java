/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.amt.common.cachemanager;

import com.amt.common.cache.CurrentDispatchOrderEntityCache;
import com.amt.common.sessions.AuthenticatedNotificationSessionManager;
import com.amt.entities.buisiness.CurrentDispatchOrderEntity;
import com.tna.common.AccessError;
import com.tna.data.Persistence;
import java.io.IOException;
import java.net.InetAddress;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.websocket.Session;
import org.json.simple.JSONObject;

/**
 *
 * @author tareq
 */
public class CurrentDispatchOrderEntityCacheManager implements Runnable {

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            long systemTime = System.currentTimeMillis();
            Timestamp now = new Timestamp(systemTime);
            Timestamp cacheTime = CurrentDispatchOrderEntityCache.getTimeStamp();
            CurrentDispatchOrderEntityCache.setTimeStamp(now);

            try {
                JSONObject differentialList = Persistence.listNewerThan(CurrentDispatchOrderEntity.class, cacheTime);
                if (differentialList != null) {
                    ArrayList<Long> changedVehicleIds = new ArrayList();
                    Set keySet = differentialList.keySet();
                    Set<String> userTokenSet = AuthenticatedNotificationSessionManager.sessionsTokenSet();

                    for (Object key : keySet) {
                        JSONObject listItem = (JSONObject) differentialList.get(key);
                        long vehicleId = (int) listItem.get("vehicleId");
                        changedVehicleIds.add(vehicleId);
                        CurrentDispatchOrderEntityCache.cache(vehicleId, listItem);

                    }
                    for (String token : userTokenSet) {
                        new Thread(() -> {
                            AuthenticatedNotificationSessionManager.lock(token);
                            try {
                                Session userSession = AuthenticatedNotificationSessionManager.get(token).getUserSession();
                                userSession.getBasicRemote().sendText("{\"server\":\""+InetAddress.getLocalHost().getHostName()+"\",\"type\":\"dispatchOrder\",\"array\":" + Arrays.toString(changedVehicleIds.toArray()) + "}");
                            } catch (IOException ex) {
                                Logger.getLogger(CurrentDispatchOrderEntityCacheManager.class.getName()).log(Level.SEVERE, null, ex);
                            } finally {
                                AuthenticatedNotificationSessionManager.unlock(token);
                            }
                        }).start();
                    }
                }
            } catch (AccessError ex) {
                handleError(ex);
            } finally {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ex) {
                    break;
                }
            }
        }
    }

    public static void handleError(AccessError ex) {

    }

    public CurrentDispatchOrderEntityCacheManager() {
        CurrentDispatchOrderEntityCache.getInstance();
    }
}
