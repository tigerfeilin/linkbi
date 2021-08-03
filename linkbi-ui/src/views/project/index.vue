<template>
  <div class="app-container">
    <el-form :model="listQuery" ref="queryForm"  :inline="true">
      <el-form-item label="应用名称" prop="searchVal">
        <el-input
          v-model="listQuery.searchVal"
          clearable
          placeholder="请输入应用名称"
          style="width: 240px;"
          size="small"
          @keyup.enter.native="fetchData"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="cyan"  icon="el-icon-search" size="mini" @click="fetchData">搜索</el-button>
        <el-button type="primary" icon="el-icon-plus" size="mini" @click="handleCreate">添加</el-button>
      </el-form-item>
    </el-form>
    <el-table
      v-loading="listLoading"
      :data="list"
      element-loading-text="Loading"

      highlight-current-row
    >
      <el-table-column align="center" label="序号" width="95">
        <template slot-scope="scope">{{ scope.$index+1 }}</template>
      </el-table-column>
      <el-table-column label="应用名称" align="center">
        <template slot-scope="scope">{{ scope.row.name }}</template>
      </el-table-column>
      <el-table-column label="应用描述" align="center">
        <template slot-scope="scope">{{ scope.row.description }}</template>
      </el-table-column>
      <el-table-column label="所属用户" width="200" align="center">
        <template slot-scope="scope">{{ scope.row.userName }}
        </template>
      </el-table-column>
      <el-table-column label="创建时间" width="200" align="center">
        <template slot-scope="scope">{{ scope.row.createTime }}</template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="230" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button type="text" icon="el-icon-edit" size="mini" @click="handleUpdate(scope.row)">
            修改
          </el-button>
          <el-button v-if="scope.row.status!='deleted'" size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)">
            删除
          </el-button>
          <el-button  size="mini" type="text" icon="el-icon-search" @click="handlerViewJob(scope.row)">
            任务列表
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="listQuery.pageNo"
      :limit.sync="listQuery.pageSize"
      @pagination="fetchData"
    />

    <el-dialog :title="textMap[dialogStatus]" :visible.sync="dialogFormVisible" width="600px">
      <el-form ref="dataForm" :rules="rules" :model="temp" label-position="left" label-width="100px">
        <el-form-item label="应用名称" prop="name">
          <el-input v-model="temp.name" placeholder="应用名称" style="width: 40%" />
        </el-form-item>
        <el-form-item label="应用描述" prop="description">
          <el-input v-model="temp.description" placeholder="应用描述" style="width: 40%" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">
          取消
        </el-button>
        <el-button type="primary" @click="dialogStatus==='create'?createData():updateData()">
          确认
        </el-button>
      </div>
    </el-dialog>
    <el-dialog :visible.sync="dialogPluginVisible" title="Reading statistics">
      <el-table :data="pluginData" border fit highlight-current-row style="width: 100%">
        <el-table-column prop="key" label="Channel" />
        <el-table-column prop="pv" label="Pv" />
      </el-table>
      <span slot="footer" class="dialog-footer">
        <el-button type="primary" @click="dialogPvVisible = false">Confirm</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import * as jobProjectApi from '@/api/project/project'

export default {
  name: 'JobProject',
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
  data() {
    return {
      list: null,
      listLoading: true,
      total: 0,
      listQuery: {
        pageNo: 1,
        pageSize: 10,
        searchVal: ''
      },
      pluginTypeOptions: ['reader', 'writer'],
      dialogPluginVisible: false,
      pluginData: [],
      dialogFormVisible: false,
      dialogStatus: '',
      textMap: {
        update: '修改',
        create: '新增'
      },
      rules: {
        name: [{ required: true, message: 'this is required', trigger: 'blur' }],
        description: [{ required: true, message: 'this is required', trigger: 'blur' }]
      },
      temp: {
        id: undefined,
        name: '',
        description: ''
      },
      visible: true
    }
  },
  created() {
    this.fetchData()
  },
  methods: {
    fetchData() {
      this.listLoading = true
      jobProjectApi.list(this.listQuery).then(response => {
        this.total = response.data.total
        this.list = response.data.records
        this.listLoading = false
      })
    },
    resetTemp() {
      this.temp = {
        id: undefined,
        name: '',
        description: ''
      }
    },
    handlerViewJob(row) {
          this.$router.push({ path: '/datasync/jobinfo', query: { projectId: row.id }})
    },
    handleCreate() {
      this.resetTemp()
      this.dialogStatus = 'create'
      this.dialogFormVisible = true
      this.$nextTick(() => {
        this.$refs['dataForm'].clearValidate()
      })
    },
    createData() {
      this.$refs['dataForm'].validate((valid) => {
        if (valid) {
          jobProjectApi.created(this.temp).then(() => {
            this.fetchData()
            this.dialogFormVisible = false
              this.msgSuccess("创建成功");
          })
        }
      })
    },
    handleUpdate(row) {
      this.temp = Object.assign({}, row) // copy obj
      this.dialogStatus = 'update'
      this.dialogFormVisible = true
      this.$nextTick(() => {
        this.$refs['dataForm'].clearValidate()
      })
    },
    updateData() {
      this.$refs['dataForm'].validate((valid) => {
        if (valid) {
          const tempData = Object.assign({}, this.temp)
          jobProjectApi.updated(tempData).then(() => {
            this.fetchData()
            this.dialogFormVisible = false
            this.msgSuccess("更新成功");
          })
        }
      })
    },
    handleDelete(row) {
      const idList = []
      idList.push(row.id)
        this.$confirm('是否确认删除:"' + row.name + '?', "警告", {
            confirmButtonText: "确定",
            cancelButtonText: "取消",
            type: "warning"
        }).then(function() {
            jobProjectApi.deleted({ idList: row.id });
        }).then(() => {
            this.fetchData();
            this.msgSuccess("删除成功");
        })
    }
  }
}
</script>
