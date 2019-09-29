package net.dgg.tac.bus.canaltomysql.bin;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

/**
 * @ClassName Dgg_orf_performance_profit_mid_bin
 * @Description TODO
 * @Auther zhangzhou
 * @Date 2019/5/28 0028  15:40
 * @Version 1.0
 **/

public class Dgg_orf_performance_profit_mid_bin {
    private Long id;
    private Date performance_time;
    private BigDecimal performance_amount;
    private Long sign_user_id;
    private int is_complete;

    public Dgg_orf_performance_profit_mid_bin() {
    }

    public Dgg_orf_performance_profit_mid_bin(Long id, Date performance_time, BigDecimal performance_amount, Long sign_user_id, int is_complete) {
        this.id = id;
        this.performance_time = performance_time;
        this.performance_amount = performance_amount;
        this.sign_user_id = sign_user_id;
        this.is_complete = is_complete;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getPerformance_time() {
        return performance_time;
    }

    public void setPerformance_time(Date performance_time) {
        this.performance_time = performance_time;
    }

    public BigDecimal getPerformance_amount() {
        return performance_amount;
    }

    public void setPerformance_amount(BigDecimal performance_amount) {
        this.performance_amount = performance_amount;
    }

    public Long getSign_user_id() {
        return sign_user_id;
    }

    public void setSign_user_id(Long sign_user_id) {
        this.sign_user_id = sign_user_id;
    }

    public int getIs_complete() {
        return is_complete;
    }

    public void setIs_complete(int is_complete) {
        this.is_complete = is_complete;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o){
            return true;
        }
        if (o == null || getClass() != o.getClass()){
            return false;
        }
        Dgg_orf_performance_profit_mid_bin that = (Dgg_orf_performance_profit_mid_bin) o;
        return is_complete == that.is_complete &&
                Objects.equals(id, that.id) &&
                Objects.equals(performance_time, that.performance_time) &&
                Objects.equals(performance_amount, that.performance_amount) &&
                Objects.equals(sign_user_id, that.sign_user_id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, performance_time, performance_amount, sign_user_id, is_complete);
    }

    @Override
    public String toString() {
        return "Dgg_orf_performance_profit_mid_bin{" +
                "id=" + id +
                ", performance_time=" + performance_time +
                ", performance_amount=" + performance_amount +
                ", sign_user_id=" + sign_user_id +
                ", is_complete=" + is_complete +
                '}';
    }
}
