/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rs.etf.sab.student.utility;

import java.math.BigDecimal;

/**
 *
 * @author matij
 */
public class ReducedPackage {
    
    private Long packageId;
    
    private Long startAddressId;
    private Long startCityId;
    
    private Long endAddressId;
    private Long endCityId;
    
    private BigDecimal weight;
    private BigDecimal price;

    public ReducedPackage(Long packageId, Long startAddressId, Long startCityId, Long endAddressId, Long endCityId, BigDecimal weight, BigDecimal price) {
        this.packageId = packageId;
        this.startAddressId = startAddressId;
        this.startCityId = startCityId;
        this.endAddressId = endAddressId;
        this.endCityId = endCityId;
        this.weight = weight;
        this.price = price;
    }

    public Long getPackageId() {
        return packageId;
    }

    public void setPackageId(Long packageId) {
        this.packageId = packageId;
    }

    public Long getStartAddressId() {
        return startAddressId;
    }

    public void setStartAddressId(Long startAddressId) {
        this.startAddressId = startAddressId;
    }

    public Long getStartCityId() {
        return startCityId;
    }

    public void setStartCityId(Long startCityId) {
        this.startCityId = startCityId;
    }

    public Long getEndAddressId() {
        return endAddressId;
    }

    public void setEndAddressId(Long endAddressId) {
        this.endAddressId = endAddressId;
    }

    public Long getEndCityId() {
        return endCityId;
    }

    public void setEndCityId(Long endCityId) {
        this.endCityId = endCityId;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
    
    
    
}
