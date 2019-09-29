package net.dgg.tac.bus.canaltomysql.bin;

import java.util.Date;
import java.util.Objects;

/**
 * @ClassName Dgg_orf_order_mid_bin
 * @Description TODO
 * @Auther zhangzhou
 * @Date 2019/5/28 0028  15:38
 * @Version 1.0
 **/

public class Dgg_orf_order_mid_bin {
    private Long id;
    private Long business_user_id;
    private String status;
    private Date place_order_time;

    public Dgg_orf_order_mid_bin() {
    }

    public Dgg_orf_order_mid_bin(Long id, Long business_user_id, String status, Date place_order_time) {
        this.id = id;
        this.business_user_id = business_user_id;
        this.status = status;
        this.place_order_time = place_order_time;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBusiness_user_id() {
        return business_user_id;
    }

    public void setBusiness_user_id(Long business_user_id) {
        this.business_user_id = business_user_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getPlace_order_time() {
        return place_order_time;
    }

    public void setPlace_order_time(Date place_order_time) {
        this.place_order_time = place_order_time;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o){
            return true;
        }
        if (o == null || getClass() != o.getClass()){
            return false;
        }
        Dgg_orf_order_mid_bin that = (Dgg_orf_order_mid_bin) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(business_user_id, that.business_user_id) &&
                Objects.equals(status, that.status) &&
                Objects.equals(place_order_time, that.place_order_time);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, business_user_id, status, place_order_time);
    }

    @Override
    public String toString() {
        return "Dgg_orf_order_mid_bin{" +
                "id=" + id +
                ", business_user_id=" + business_user_id +
                ", status='" + status + '\'' +
                ", place_order_time=" + place_order_time +
                '}';
    }
}
