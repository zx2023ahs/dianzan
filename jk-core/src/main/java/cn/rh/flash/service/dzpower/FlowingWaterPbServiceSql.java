package cn.rh.flash.service.dzpower;

public class FlowingWaterPbServiceSql {


    /**
     * 条件查询 当前用户最后一条返佣记录
     * @param uid
     * @param taskIdw
     * @return
     */
    public static String getFlowingWaterPb(Long uid, String taskIdw) {

        return String.format(
                " select * from t_dzgoods_flowingwaterpb where uid = %s and task_idw = %s order by flowing_water_date desc limit 1 ",
                uid,taskIdw
        );
    }
}
