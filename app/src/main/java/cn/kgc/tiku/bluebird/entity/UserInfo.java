package cn.kgc.tiku.bluebird.entity;


import java.util.List;

import cn.kgc.tiku.bluebird.entity.result.AbstractResult;

public class UserInfo extends AbstractResult {

    private String clothesCenter;
    private String clothesDown;
    private String clothesUp;
    private String email;
    private boolean isCreatedImage;
    private boolean isOpenProductDialog;
    private String passport;
    private List<ProductList> productList;
    private int sexType;
    private String title;
    private int urgencyNoticeInterval;
    private long userId;
    private String userName;
    private String userNick;
    private int userRecordSexType;
    private int userType;

    public void setClothesCenter(String clothesCenter) {
        this.clothesCenter = clothesCenter;
    }

    public String getClothesCenter() {
        return clothesCenter;
    }

    public void setClothesDown(String clothesDown) {
        this.clothesDown = clothesDown;
    }

    public String getClothesDown() {
        return clothesDown;
    }

    public void setClothesUp(String clothesUp) {
        this.clothesUp = clothesUp;
    }

    public String getClothesUp() {
        return clothesUp;
    }


    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setIsCreatedImage(boolean isCreatedImage) {
        this.isCreatedImage = isCreatedImage;
    }

    public boolean getIsCreatedImage() {
        return isCreatedImage;
    }

    public void setIsOpenProductDialog(boolean isOpenProductDialog) {
        this.isOpenProductDialog = isOpenProductDialog;
    }

    public boolean getIsOpenProductDialog() {
        return isOpenProductDialog;
    }


    public void setPassport(String passport) {
        this.passport = passport;
    }

    public String getPassport() {
        return passport;
    }

    public void setProductList(List<ProductList> productList) {
        this.productList = productList;
    }

    public List<ProductList> getProductList() {
        return productList;
    }

    public void setSexType(int sexType) {
        this.sexType = sexType;
    }

    public int getSexType() {
        return sexType;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setUrgencyNoticeInterval(int urgencyNoticeInterval) {
        this.urgencyNoticeInterval = urgencyNoticeInterval;
    }

    public int getUrgencyNoticeInterval() {
        return urgencyNoticeInterval;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserNick(String userNick) {
        this.userNick = userNick;
    }

    public String getUserNick() {
        return userNick;
    }

    public void setUserRecordSexType(int userRecordSexType) {
        this.userRecordSexType = userRecordSexType;
    }

    public int getUserRecordSexType() {
        return userRecordSexType;
    }

    public void setUserType(int userType) {
        this.userType = userType;
    }

    public int getUserType() {
        return userType;
    }

}