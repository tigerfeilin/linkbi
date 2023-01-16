<template>
  <div v-loading="loading" class="app-container">
    <el-form :model="listQuery" ref="queryForm"  :inline="true">
      <el-form-item label="看板名称" prop="searchVal">
        <el-input
          v-model="listQuery.name"
          clearable
          placeholder="请输入看板名称"
          style="width: 240px;"
          size="small"
          @keyup.enter.native="getList"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="cyan"  icon="el-icon-search" size="mini" @click="getList">搜索</el-button>
        <el-button type="primary" icon="el-icon-plus" size="mini" @click="addDashboard">添加</el-button>
      </el-form-item>
    </el-form>
    <el-row>
      <el-col :span="4" v-for="(item) in dashboardList" :key="item.id" :offset="0.5" >
        <div style="margin:10px">
          <el-card :body-style="{ padding: '0px'}" shadow="hover">
            <div style="padding: 10px">
              <span class="boardText">{{item.name}}</span><br>
              <div class="bottom clearfix">
                <time class="time">{{ item.updateTime }}</time><br>
                <el-button type="text" size="small" icon="el-icon-view" @click="viewDashboard(item)">浏览</el-button>
                <el-button v-if="checkPermi(['dataview:dashboard:update'])" type="text" size="small" icon="el-icon-edit" @click="editDashboard(item)">编辑</el-button>
                <el-button v-if="checkPermi(['dataview:dashboard:delete'])" type="text" size="small" icon="el-icon-delete" @click="deleteDashboard(item)">删除</el-button>
              </div>
            </div>
          </el-card>
        </div>
      </el-col>
    </el-row>
    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="listQuery.current"
      :limit.sync="listQuery.size"
      @pagination="getList"
    />
    <!--<dashboardItem :dashboard="currentDashboard" mode="edit" />-->
    <el-dialog :title="$t('dashboard.addOrEditDashboard')" width="750px" :visible.sync="editDialogVisible">
      <el-form ref="dbObj" :model="dbObj" label-width="160px" :rules="rules">
        <el-form-item :label="$t('dashboard.dashboardName')" prop="name">
          <el-input v-model="dbObj.name" size="small" style="width: 450px;" :placeholder="$t('dashboard.dashboardNamePlaceholder')" />
        </el-form-item>
        <el-form-item :label="$t('dashboard.dashboardDesc')" prop="comment">
          <el-input v-model="dbObj.comment" type="textarea" :rows="5" size="small" style="width: 450px;" :placeholder="$t('dashboard.dashboardDescPlaceholder')" />
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button type="primary" size="small" @click="editDialogVisible = false"> {{ $t('common.cancel') }}</el-button>
        <el-button type="primary" size="small" @click="handleSubmit"> {{ $t('common.confirm') }}</el-button>
      </span>
    </el-dialog>
  </div>
</template>
<script>
import draggable from 'vuedraggable'
import dashboardItem from './dbItem'
import { addDashboard, updateDashboard, getDashboardList, deleteDashboard, dbOrder } from '@/api/dataview/dashboard'
export default {
  components: { dashboardItem, draggable },
  data() {
    return {
      dashboardList: [],
      total:0,
      currentDashboard: undefined,
      editDialogVisible: false,
      dbObj: {},
      loading: false,
      isCollapse: false,
      listQuery:{
          current: 1,
          size: 10,
          name:undefined,
      },
        rules: {
            name: [{ required: true, message: 'this is required', trigger: 'change' }],
            comment: [{ required: true, message: 'this is required', trigger: 'change' }]
        }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
        getDashboardList(this.listQuery).then(resp => {
        this.loading = false
        this.dashboardList = resp.data.rows
        this.total = resp.data.total
          /*
        resp.data.order.forEach((id, index) => {
          const itemIndex = resp.data.dashboards.findIndex(item => item.dashboard_id === id)
          if (itemIndex >= 0) {
            this.dashboardList.push(resp.data.dashboards[itemIndex])
            resp.data.dashboards.splice(itemIndex, 1)
          } else {
            console.log(id, index)
          }
        })
        this.dashboardList = this.dashboardList.concat(resp.data.dashboards)
        const dashboard = this.dashboardList.find(item => item.dashboard_id === this.$route.query.id)
        if (dashboard) {
          this.currentDashboard = dashboard
        } else {

        }*/
        //console.log(this.dashboardList)
        this.currentDashboard = this.dashboardList[0]
        //if (this.currentDashboard) {
          //this.$router.push('/dashboard?id=${this.currentDashboard.dashboard_id}').catch(_ => {})
        //}
      })
    },
    switchDb(db) {
      if (db.id === this.currentDashboard.id) {
        this.getList()
        return
      }
      // this.$confirm('确定要离开当前页面吗?系统可能不会保存您所做的更改。', '提示').then(() => {
      this.currentDashboard = db
      this.$router.push('/dataview/editdb/' + this.currentDashboard.id)
      // })
    },
    addDashboard() {
      this.dbObj = {}
      this.editDialogVisible = true
    },
    viewDashboard(db) {
          //this.dbObj = Object.assign({}, db)
          this.currentDashboard = db
          this.$router.push('/dataview/viewdb/'+ this.currentDashboard.id)
    },
    editDashboard(db) {
      //this.dbObj = Object.assign({}, db)
        this.currentDashboard = db
        this.$router.push('/dataview/editdb/'+ this.currentDashboard.id)
    },
    handleCommand(cmd) {
      if (cmd.type === 'edit') {
        this.editDashboard(cmd.target)
      } else {
        this.deleteDashboard(cmd.target)
      }
    },
    handleSubmit() {
      this.$refs.dbObj.validate(valid => {
          if (valid) {
              if (this.dbObj.id) {
                  updateDashboard(this.dbObj).then(resp => {
                      this.getList()
                      this.editDialogVisible = false
                  })
              } else {
                  addDashboard(this.dbObj).then(resp => {
                      this.getList()
                      this.editDialogVisible = false
                  })
              }
          }
      });
    },
    handleOrderChange(evt) {
      const data = {
        order: this.dashboardList.map(item => item.dashboard_id)
      }
      dbOrder(data)
    },
    deleteDashboard(db) {
      const dashboardId = db.id
      this.$confirm(this.$t('dashboard.deleteConfirm') +db.name, this.$t('common.confirm')).then(() => {
        deleteDashboard(db.id).then(() => {
          this.getList()
          this.msgSuccess(this.$t('common.deleteSuccess'))
        })
      })
    }
  },
}
</script>

<style lang="scss" scoped>
  .time {
    font-size: 12px;
    color: #999;
  }
  .boardText {
    font-size: 14px;
    word-break: break-all;
    text-overflow: ellipsis;
    overflow: hidden;
    display: -webkit-box;
    -webkit-line-clamp: 1;//控制行数
    -webkit-box-orient: vertical;
  }
  .bottom {
    margin-top: 13px;
    line-height: 12px;
  }

  .button {
    padding: 0;
    float: right;
  }

  .image {
    width: 100%;
    display: block;
  }

  .clearfix:before,
  .clearfix:after {
    display: table;
    content: "";
  }

  .clearfix:after {
    clear: both
  }
</style>
