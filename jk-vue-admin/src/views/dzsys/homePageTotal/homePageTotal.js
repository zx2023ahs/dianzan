import homePageTotalApi from '@/api/dzsys/homePageTotal'
import permission from '@/directive/permission/index.js'

export default {
  //如果需要标签页缓存生效，则需要保证name值和菜单管理中的编码值一致
  name: 'homePageTotal',
  directives: { permission },
  data() {
    return {
      formVisible: false,
      formTitle: '添加首页统计',
      isAdd: true,
      form: {
        day:'',
        registrationNum:'',
        vipNum:'',
        vipFirstNum:'',
        cNum:'',
        cMoney:'',
        tNum:'',
        tMoney:'',
        money:'',
        l1Vip:'',
        l2Vip:'',
        l3Vip:'',
        l1Pb:'',
        l2Pb:'',
        l3Pb:'',
        totalPb:'',
        dcMoney:'',
        pbNum:'',
        id: ''
      },
      listQuery: {
        page: 1,
        limit: 20,
        id: undefined
      },
      total: 0,
      list: null,
      listLoading: true,
      selRow: {}
    }
  },
  filters: {
    statusFilter(status) {
      const statusMap = {
        published: 'success',
        draft: 'gray',
        deleted: 'danger'
      }
      return statusMap[status]
    }
  },
  computed: {

    //表单验证
    rules() {
      return {
        // cfgName: [
        //   { required: true, message: this.$t('config.name') + this.$t('common.isRequired'), trigger: 'blur' },
        //   { min: 3, max: 2000, message: this.$t('config.name') + this.$t('config.lengthValidation'), trigger: 'blur' }
        // ]
      }
    }
  },
  created() {
    this.init()
  },
  methods: {
    init() {
      this.fetchData()
    },
    fetchData() {
      this.listLoading = true
        homePageTotalApi.getList(this.listQuery).then(response => {
        this.list = response.data.records
        this.listLoading = false
        this.total = response.data.total
      })
    },
    search() {
      this.fetchData()
    },
    reset() {
      this.listQuery.id = ''
      this.fetchData()
    },
    handleFilter() {
      this.listQuery.page = 1
      this.getList()
    },
    handleClose() {

    },
    fetchNext() {
      this.listQuery.page = this.listQuery.page + 1
      this.fetchData()
    },
    fetchPrev() {
      this.listQuery.page = this.listQuery.page - 1
      this.fetchData()
    },
    fetchPage(page) {
      this.listQuery.page = page
      this.fetchData()
    },
    changeSize(limit) {
      this.listQuery.limit = limit
      this.fetchData()
    },
    handleCurrentChange(currentRow, oldCurrentRow) {
      this.selRow = currentRow
    },
    resetForm() {
      this.form = {
        day:'',
        registrationNum:'',
        vipNum:'',
        vipFirstNum:'',
        cNum:'',
        cMoney:'',
        tNum:'',
        tMoney:'',
        money:'',
        l1Vip:'',
        l2Vip:'',
        l3Vip:'',
        l1Pb:'',
        l2Pb:'',
        l3Pb:'',
        totalPb:'',
        dcMoney:'',
        pbNum:'',
        id: ''
      }
    },
    add() {
      this.formTitle = '添加首页统计'
      this.formVisible = true
      this.isAdd = true

      if(this.$refs['form'] !== undefined) {
        this.$refs['form'].resetFields()
      }
      // 如果表单初始化有特殊处理需求,可以在resetForm中处理
      this.resetForm()
    },
    save() {
      this.$refs['form'].validate((valid) => {
        if (valid) {
            const formData = {
                id:this.form.id,
                day:this.form.day,
                registrationNum:this.form.registrationNum,
                vipNum:this.form.vipNum,
                vipFirstNum:this.form.vipFirstNum,
                cNum:this.form.cNum,
                cMoney:this.form.cMoney,
                tNum:this.form.tNum,
                tMoney:this.form.tMoney,
                money:this.form.money,
                l1Vip:this.form.l1Vip,
                l2Vip:this.form.l2Vip,
                l3Vip:this.form.l3Vip,
                l1Pb:this.form.l1Pb,
                l2Pb:this.form.l2Pb,
                l3Pb:this.form.l3Pb,
                totalPb:this.form.totalPb,
                dcMoney:this.form.dcMoney,
                pbNum:this.form.pbNum,
            }
            if(formData.id){
                homePageTotalApi.update(formData).then(response => {
                    this.$message({
                        message: this.$t('common.optionSuccess'),
                        type: 'success'
                    })
                    this.fetchData()
                    this.formVisible = false
                })
            }else{
                homePageTotalApi.add(formData).then(response => {
                    this.$message({
                        message: this.$t('common.optionSuccess'),
                        type: 'success'
                    })
                    this.fetchData()
                    this.formVisible = false
                })
            }
        } else {
          return false
        }
      })
    },
    checkSel() {
      if (this.selRow && this.selRow.id) {
        return true
      }
      this.$message({
        message: this.$t('common.mustSelectOne'),
        type: 'warning'
      })
      return false
    },
    editItem(record){
      this.selRow = record
      this.edit()
    },
    edit() {
      if (this.checkSel()) {
        this.isAdd = false
        let form = Object.assign({}, this.selRow)
        this.form = form
        this.formTitle = '编辑首页统计'
        this.formVisible = true

        if(this.$refs['form'] !== undefined) {
          this.$refs['form'].resetFields()
        }
      }
    },
    removeItem(record){
      this.selRow = record
      this.remove()
    },
    remove() {
      if (this.checkSel()) {
        var id = this.selRow.id
        this.$confirm(this.$t('common.deleteConfirm'), this.$t('common.tooltip'), {
          confirmButtonText: this.$t('button.submit'),
          cancelButtonText: this.$t('button.cancel'),
          type: 'warning'
        }).then(() => {
            homePageTotalApi.remove(id).then(response => {
            this.$message({
              message: this.$t('common.optionSuccess'),
              type: 'success'
            })
            this.fetchData()
          }).catch( err=> {
            this.$notify.error({
              title: '错误',
              message: err
            })
          })
        }).catch(() => {
        })
      }
    }

  }
}
