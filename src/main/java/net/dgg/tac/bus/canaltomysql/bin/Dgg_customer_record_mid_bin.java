package net.dgg.tac.bus.canaltomysql.bin;

import java.util.Date;
import java.util.Objects;

/**
 * @ClassName Dgg_customer_record_mid_bin
 * @Description TODO
 * @Auther zhangzhou
 * @Date 2019/5/28 0028  15:33
 * @Version 1.0
 **/

public class Dgg_customer_record_mid_bin {
    private Long id;
    private String ask_employee_no;
    private Date visit_time;
    private Date promissory_time;
    private int status;
    private Date create_time;
    private Date update_time;

    public Dgg_customer_record_mid_bin() {

    }

    public Dgg_customer_record_mid_bin(Long id, String ask_employee_no, Date visit_time, Date promissory_time, int status, Date create_time, Date update_time) {
        this.id = id;
        this.ask_employee_no = ask_employee_no;
        this.visit_time = visit_time;
        this.promissory_time = promissory_time;
        this.status = status;
        this.create_time = create_time;
        this.update_time = update_time;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAsk_employee_no() {
        return ask_employee_no;
    }

    public void setAsk_employee_no(String ask_employee_no) {
        this.ask_employee_no = ask_employee_no;
    }

    public Date getVisit_time() {
        return visit_time;
    }

    public void setVisit_time(Date visit_time) {
        this.visit_time = visit_time;
    }

    public Date getPromissory_time() {
        return promissory_time;
    }

    public void setPromissory_time(Date promissory_time) {
        this.promissory_time = promissory_time;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
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

    @Override
    public boolean equals(Object o) {
        if (this == o){
            return true;
        }
        if (o == null || getClass() != o.getClass()){
            return false;
        }
        Dgg_customer_record_mid_bin that = (Dgg_customer_record_mid_bin) o;
        return status == that.status &&
                Objects.equals(id, that.id) &&
                Objects.equals(ask_employee_no, that.ask_employee_no) &&
                Objects.equals(visit_time, that.visit_time) &&
                Objects.equals(promissory_time, that.promissory_time) &&
                Objects.equals(create_time, that.create_time) &&
                Objects.equals(update_time, that.update_time);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, ask_employee_no, visit_time, promissory_time, status, create_time, update_time);
    }

    @Override
    public String toString() {
        return "Dgg_customer_record_mid_bin{" +
                "id=" + id +
                ", ask_employee_no='" + ask_employee_no + '\'' +
                ", visit_time=" + visit_time +
                ", promissory_time=" + promissory_time +
                ", status=" + status +
                ", create_time=" + create_time +
                ", update_time=" + update_time +
                '}';
    }
}
