package com.example.domo.model;

public class Place {

    private Long id;
    private String name;
    private String category;
    private String address;
    private double lat;
    private double lng;
    private String sido;
    private String sigungu;

    // ====== 정렬/점수 계산에 필요한 지표 ======
    private double distance;
    private int discountPercent;
    private int popularity;       // 없으면 0
    private int distanceScore;
    private int benefitScore;
    private int popularScore;
    private int totalScore;


    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    // ====== getters / setters ======
    public double getDistance() { return distance; }
    public void setDistance(double distance) { this.distance = distance; }

    public int getDiscountPercent() { return discountPercent; }
    public void setDiscountPercent(int discountPercent) { this.discountPercent = discountPercent; }

    public int getPopularity() { return popularity; }
    public void setPopularity(int popularity) { this.popularity = popularity; }

    public int getDistanceScore() { return distanceScore; }
    public void setDistanceScore(int distanceScore) { this.distanceScore = distanceScore; }

    public int getBenefitScore() { return benefitScore; }
    public void setBenefitScore(int benefitScore) { this.benefitScore = benefitScore; }

    public int getPopularScore() { return popularScore; }
    public void setPopularScore(int popularScore) { this.popularScore = popularScore; }

    public int getTotalScore() { return totalScore; }
    public void setTotalScore(int totalScore) { this.totalScore = totalScore; }

    public void updateScores(int distanceScore, int benefitScore, int popularScore) {
        this.distanceScore = distanceScore;
        this.benefitScore = benefitScore;
        this.popularScore = popularScore;
        this.totalScore   = distanceScore + benefitScore + popularScore;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public double getLat() { return lat; }
    public void setLat(double lat) { this.lat = lat; }

    public double getLng() { return lng; }
    public void setLng(double lng) { this.lng = lng; }

    public String getSido() { return sido; }
    public void setSido(String sido) { this.sido = sido; }

    public String getSigungu() { return sigungu; }
    public void setSigungu(String sigungu) { this.sigungu = sigungu; }

}
