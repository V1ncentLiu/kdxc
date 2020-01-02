var trackingDialogTempate=
    '<div>'
    +'<el-dialog title="跟进记录"  :visible.sync="param.trackingDialogVisible" width="1000px">'
    +'<template>'
    +'<el-table :data="param.tableData" border style="width: 100%" ref="releaseTable">'
    +'<el-table-column prop="xh" label="序号" width="55" type="index" align="center"></el-table-column>'
    +'<el-table-column prop="callTime" width="150" label="跟进时间" align="center"></el-table-column>'
    +'<el-table-column prop="customerStatusName" width="100" label="客户状态"  align="center"></el-table-column>'
    +'<el-table-column prop="isCall" width="90" :formatter="formatIsCall" label="是否接通" align="center"></el-table-column>'
    // +'<el-table-column prop="resourceStatus" :formatter="formatResourceStatus" label="资源有效性"  align="center" show-overflow-tooltip></el-table-column>'
    // +'<el-table-column prop="invitationStatus" :formatter="formatInvitationStatus" label="是否邀约"  align="center" show-overflow-tooltip></el-table-column>'
    +'<el-table-column prop="focusPoint" label="客户关注点" align="center" show-overflow-tooltip></el-table-column>'
    //+'<el-table-column prop="visitStatus" :formatter="formatVisitStatus" label="是否继续回访"  align="center" show-overflow-tooltip></el-table-column>'
    +'<el-table-column prop="nextVisitTime" label="下次回访时间" width="150" align="center"></el-table-column>'
    +'<el-table-column prop="createUserName" label="创建人" align="center" width="130"></el-table-column>'
    +'</el-table>'
    +'</template>'
    +'</el-dialog>'
    +'</div>'

Vue.component('dialog-tracking',{
    template:trackingDialogTempate,
    props:['param'],
    computed: {
        data(){
            console.log("sdsdfsdfs:"+this.param)
            return this.param.tableData
        }
    },
    watch: {},
    methods:{
        formatCustomerStatus(row, column){
            //客户状态
        },
        formatIsCall(row, column){
            //是否接通
            console.log(row)
            return row.isCall==0?"否": row.isCall==1?"是":""
        },
        formatResourceStatus(row, column){
            // 资源有效
            return row.customerStatus==1?"有效资源": "无效资源"
        },
        formatInvitationStatus(row, column){
            // 是否邀约
            return row.invitationStatus==1?"是": "否"
        },
        formatVisitStatus(row, column){
            // 是否继续回访
            return row.visitStatus==1?"是": "否"
        }
    }
});



