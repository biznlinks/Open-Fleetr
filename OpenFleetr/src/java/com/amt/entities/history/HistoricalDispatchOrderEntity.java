/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.amt.entities.history;

import com.tna.entities.BasicEntity;
import java.sql.Date;

/**
 *
 * @author tareq
 */
public class HistoricalDispatchOrderEntity  extends BasicEntity {
  
  double startLatitude;
  double startLongitude;
  double destinationLatitude;
  double destinationLongitude;
  Date creationDate;
  Date completionDate;
  long vehicleId;
  long status;

}

