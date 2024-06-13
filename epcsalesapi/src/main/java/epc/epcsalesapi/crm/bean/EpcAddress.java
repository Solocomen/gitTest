/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package epc.epcsalesapi.crm.bean;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


/**
 *
 * @author KenTKChung
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class EpcAddress implements Serializable {

    private static final long serialVersionUID = 1L;
    private String addressId;
    private String addressType;
    private String floor;
    private String floorDesc;
    private String block;
    private String building;
    private String estate;
    private String streetNo;
    private String street;
    private String lot;
    private String district;
    private String area;
    private String floorChi;
    private String floorDescChi;
    private String blockChi;
    private String buildingChi;
    private String estateChi;
    private String streetNoChi;
    private String streetChi;
    private String lotChi;
    private String distinctChi;
    private String areaChi;
    private String addrLine1;
    private String addrLine2;
    private String addrLine1Chi;
    private String addrLine2Chi;
    private String xCoordinate;
    private String yCoordinate;
    private String addressCode;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date createdDate;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date lastUpdatedDate;
    private String createdBy;
    private String lastUpdatedBy;
    private String flat;
    private String flatDesc;
    private String blockDesc;
    private String flatChi;
    private String flatDescChi;
    private String blockDescChi;
    private String addressNature;

    public EpcAddress() {
    }

    public EpcAddress(String addressId) {
        this.addressId = addressId;
    }

    public String getAddressId() {
        return addressId;
    }

    public void setAddressId(String addressId) {
        this.addressId = addressId;
    }

    public String getAddressType() {
        return addressType;
    }

    public void setAddressType(String addressType) {
        this.addressType = addressType;
    }

    public String getFloor() {
        return floor;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

    public String getFloorDesc() {
        return floorDesc;
    }

    public void setFloorDesc(String floorDesc) {
        this.floorDesc = floorDesc;
    }

    public String getBlock() {
        return block;
    }

    public void setBlock(String block) {
        this.block = block;
    }

    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public String getEstate() {
        return estate;
    }

    public void setEstate(String estate) {
        this.estate = estate;
    }

    public String getStreetNo() {
        return streetNo;
    }

    public void setStreetNo(String streetNo) {
        this.streetNo = streetNo;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getLot() {
        return lot;
    }

    public void setLot(String lot) {
        this.lot = lot;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getFloorChi() {
        return floorChi;
    }

    public void setFloorChi(String floorChi) {
        this.floorChi = floorChi;
    }

    public String getFloorDescChi() {
        return floorDescChi;
    }

    public void setFloorDescChi(String floorDescChi) {
        this.floorDescChi = floorDescChi;
    }

    public String getBlockChi() {
        return blockChi;
    }

    public void setBlockChi(String blockChi) {
        this.blockChi = blockChi;
    }

    public String getBuildingChi() {
        return buildingChi;
    }

    public void setBuildingChi(String buildingChi) {
        this.buildingChi = buildingChi;
    }

    public String getEstateChi() {
        return estateChi;
    }

    public void setEstateChi(String estateChi) {
        this.estateChi = estateChi;
    }

    public String getStreetNoChi() {
        return streetNoChi;
    }

    public void setStreetNoChi(String streetNoChi) {
        this.streetNoChi = streetNoChi;
    }

    public String getStreetChi() {
        return streetChi;
    }

    public void setStreetChi(String streetChi) {
        this.streetChi = streetChi;
    }

    public String getLotChi() {
        return lotChi;
    }

    public void setLotChi(String lotChi) {
        this.lotChi = lotChi;
    }

    public String getDistinctChi() {
        return distinctChi;
    }

    public void setDistinctChi(String distinctChi) {
        this.distinctChi = distinctChi;
    }

    public String getAreaChi() {
        return areaChi;
    }

    public void setAreaChi(String areaChi) {
        this.areaChi = areaChi;
    }

    public String getAddrLine1() {
        return addrLine1;
    }

    public void setAddrLine1(String addrLine1) {
        this.addrLine1 = addrLine1;
    }

    public String getAddrLine2() {
        return addrLine2;
    }

    public void setAddrLine2(String addrLine2) {
        this.addrLine2 = addrLine2;
    }

    public String getAddrLine1Chi() {
        return addrLine1Chi;
    }

    public void setAddrLine1Chi(String addrLine1Chi) {
        this.addrLine1Chi = addrLine1Chi;
    }

    public String getAddrLine2Chi() {
        return addrLine2Chi;
    }

    public void setAddrLine2Chi(String addrLine2Chi) {
        this.addrLine2Chi = addrLine2Chi;
    }

    public String getXCoordinate() {
        return xCoordinate;
    }

    public void setXCoordinate(String xCoordinate) {
        this.xCoordinate = xCoordinate;
    }

    public String getYCoordinate() {
        return yCoordinate;
    }

    public void setYCoordinate(String yCoordinate) {
        this.yCoordinate = yCoordinate;
    }

    public String getAddressCode() {
        return addressCode;
    }

    public void setAddressCode(String addressCode) {
        this.addressCode = addressCode;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getLastUpdatedDate() {
        return lastUpdatedDate;
    }

    public void setLastUpdatedDate(Date lastUpdatedDate) {
        this.lastUpdatedDate = lastUpdatedDate;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    public void setLastUpdatedBy(String lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }

    public String getFlat() {
        return flat;
    }

    public void setFlat(String flat) {
        this.flat = flat;
    }

    public String getFlatDesc() {
        return flatDesc;
    }

    public void setFlatDesc(String flatDesc) {
        this.flatDesc = flatDesc;
    }

    public String getBlockDesc() {
        return blockDesc;
    }

    public void setBlockDesc(String blockDesc) {
        this.blockDesc = blockDesc;
    }

    public String getFlatChi() {
        return flatChi;
    }

    public void setFlatChi(String flatChi) {
        this.flatChi = flatChi;
    }

    public String getFlatDescChi() {
        return flatDescChi;
    }

    public void setFlatDescChi(String flatDescChi) {
        this.flatDescChi = flatDescChi;
    }

    public String getBlockDescChi() {
        return blockDescChi;
    }

    public void setBlockDescChi(String blockDescChi) {
        this.blockDescChi = blockDescChi;
    }
    
    public String getAddressNature() {
        return addressNature;
    }

    public void setAddressNature(String addressNature) {
        this.addressNature = addressNature;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (addressId != null ? addressId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof EpcAddress)) {
            return false;
        }
        EpcAddress other = (EpcAddress) object;
        if ((this.addressId == null && other.addressId != null) || (this.addressId != null && !this.addressId.equals(other.addressId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "epc.jpa.EpcAddress[ addressId=" + addressId + " ]";
    }
    
}
