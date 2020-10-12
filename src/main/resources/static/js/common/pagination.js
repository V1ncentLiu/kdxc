var paginationTempate= '<div>' 
     +  '<el-pagination @size-change="handleSizeChange" background  '
     +  '        @current-change="handleCurrentChange" :current-page="pager.currentPage" '
     +  '       :page-sizes="papernum" :page-size="pager.pageSize" '
     +  '           layout="total, sizes, prev, pager, next, jumper" :total="pager.total"> '
     +  '       </el-pagination>'
     +'</div>'

Vue.component('table-pagination',{
    template:paginationTempate,
    // props:['pager'],
    props:{
      pager: Object,
      papernum: {
        type: Array,
        default: () => [20, 50, 100 ,500]
      },
    },
    computed: {
       total() {
          return this.pager.total;
       },
        initBack() {
             return this.pager.total / this.pager.pageSize < this.pager.currentPage;
        }
    }, 
     watch: { 
       total() { 
         // 存在记录但未获取到数据时, 重新请求 
          if (this.initBack) { 
            // this.pager.currentPage -= 1; this.$emit('change');
           } 
       },
     },

    methods:{
      handleSizeChange(val) {
          //下拉框  每页 10,20条切换 调用
          this.pager.currentPage = 1;
          this.pager.pageSize=val;
          localStorage.setItem('allChangePageSize', val);
          this.$emit('change');
      },
      handleCurrentChange(val) {
          //点击 页码
        this.pager.currentPage = val;
        this.$emit('change');
      },
    }
});



