/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.amt.entities;

import java.sql.Date;

/**
 *
 * @author tareq
 */
public class HistoricalStatusEntity {
    
    public long vehicleId;
    public long driverId;
    public Date checkOutDate;
    public Date checkInDate;
    public long status;
    public String notes;
    
}
