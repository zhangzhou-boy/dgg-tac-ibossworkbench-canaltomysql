package net.dgg.tac.bus.canaltomysql.bin;


import java.util.Date;
import java.util.Objects;

/**
 * @ClassName Bus_business_bean
 * @Description TODO
 * @Auther zhangzhou
 * @Date 2019/5/28 0028  15:22
 * @Version 1.0
 **/

public class Dgg_bus_business_mid_bin {
    private Long id;
    private Date create_time;
    private Date update_time;
    private String no;
    private Long follower_id;
    private String customer_name;
    private String customer_no;
    private String customer_phone;
    private Date next_follow_time;
    private String business_status;
    private Date will_drop_time;
    private Long follower_organization_id;
    private String way_code;
    private Date distribution_time;
    private String table_name;

    public Dgg_bus_business_mid_bin() {
    }

    public Dgg_bus_business_mid_bin(Long id, Date create_time, Date update_time, String no, Long follower_id, String customer_name, String customer_no, String customer_phone, Date next_follow_time, String business_status, Date will_drop_time, Long follower_organization_id, String way_code, Date distribution_time, String table_name) {
        this.id = id;
        this.create_time = create_time;
        this.update_time = update_time;
        this.no = no;
        this.follower_id = follower_id;
        this.customer_name = customer_name;
        this.customer_no = customer_no;
        this.customer_phone = customer_phone;
        this.next_follow_time = next_follow_time;
        this.business_status = business_status;
        this.will_drop_time = will_drop_time;
        this.follower_organization_id = follower_organization_id;
        this.way_code = way_code;
        this.distribution_time = distribution_time;
        this.table_name = table_name;
    }

    @Override
    public String toString() {
        return "Bus_business_bean{" +
                "id=" + id +
                ", create_time=" + create_time +
                ", update_time=" + update_time +
                ", no='" + no + '\'' +
                ", follower_id=" + follower_id +
                ", customer_name='" + customer_name + '\'' +
                ", customer_no='" + customer_no + '\'' +
                ", customer_phone='" + customer_phone + '\'' +
                ", next_follow_time=" + next_follow_time +
                ", business_status='" + business_status + '\'' +
                ", will_drop_time=" + will_drop_time +
                ", follower_organization_id=" + follower_organization_id +
                ", way_code='" + way_code + '\'' +
                ", distribution_time=" + distribution_time +
                ", table_name='" + table_name + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()){
            return false;
        }
        Dgg_bus_business_mid_bin that = (Dgg_bus_business_mid_bin) o;
        return id.equals(that.id) &&
                create_time.equals(that.create_time) &&
                update_time.equals(that.update_time) &&
                no.equals(that.no) &&
                follower_id.equals(that.follower_id) &&
                customer_name.equals(that.customer_name) &&
                customer_no.equals(that.customer_no) &&
                customer_phone.equals(that.customer_phone) &&
                next_follow_time.equals(that.next_follow_time) &&
                business_status.equals(that.business_status) &&
                will_drop_time.equals(that.will_drop_time) &&
                follower_organization_id.equals(that.follower_organization_id) &&
                way_code.equals(that.way_code) &&
                distribution_time.equals(that.distribution_time) &&
                table_name.equals(that.table_name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, create_time, update_time, no, follower_id, customer_name, customer_no, customer_phone, next_follow_time, business_status, will_drop_time, follower_organization_id, way_code, distribution_time, table_name);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Date create_time) {
        this.create_time = create_time;
    }

    public Date getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(Date update_time) {
        this.update_time = update_time;
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public Long getFollower_id() {
        return follower_id;
    }

    public void setFollower_id(Long follower_id) {
        this.follower_id = follower_id;
    }

    public String getCustomer_name() {
        return customer_name;
    }

    public void setCustomer_name(String customer_name) {
        this.customer_name = customer_name;
    }

    public String getCustomer_no() {
        return customer_no;
    }

    public void setCustomer_no(String customer_no) {
        this.customer_no = customer_no;
    }

    public String getCustomer_phone() {
        return customer_phone;
    }

    public void setCustomer_phone(String customer_phone) {
        this.customer_phone = customer_phone;
    }

    public Date getNext_follow_time() {
        return next_follow_time;
    }

    public void setNext_follow_time(Date next_follow_time) {
        this.next_follow_time = next_follow_time;
    }

    public String getBusiness_status() {
        return business_status;
    }

    public void setBusiness_status(String business_status) {
        this.business_status = business_status;
    }

    public Date getWill_drop_time() {
        return will_drop_time;
    }

    public void setWill_drop_time(Date will_drop_time) {
        this.will_drop_time = will_drop_time;
    }

    public Long getFollower_organization_id() {
        return follower_organization_id;
    }

    public void setFollower_organization_id(Long follower_organization_id) {
        this.follower_organization_id = follower_organization_id;
    }

    public String getWay_code() {
        return way_code;
    }

    public void setWay_code(String way_code) {
        this.way_code = way_code;
    }

    public Date getDistribution_time() {
        return distribution_time;
    }

    public void setDistribution_time(Date distribution_time) {
        this.distribution_time = distribution_time;
    }

    public String getTable_name() {
        return table_name;
    }

    public void setTable_name(String table_name) {
        this.table_name = table_name;
    }
}
