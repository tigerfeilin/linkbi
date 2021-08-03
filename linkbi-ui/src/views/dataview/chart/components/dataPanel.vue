<template>
  <div>
    <el-form label-position="top" class="panel" style="text-align:left;">
      <el-form-item v-show="dataSrcVisible" :label="$t('common.dataSource')+':'">
        <el-select v-model="modeId" size="mini" filterable :placeholder="$t('dataSource.sourcePlaceholder')" style="width:200px;" clearable @change="handleTableChange">
          <el-option v-for="item in tableList" :key="item.id" :label="item.modeName" :value="item.id" />
        </el-select>
      </el-form-item>
      <!--
      <el-form-item>
        <el-select v-show="dataSrcVisible" v-model="selectedTable" :disabled="!selectedBase" size="mini" filterable :placeholder="$t('dataSource.tablePlaceholder')" style="width:200px;" clearable @change="handleDataSrcChange">
          <el-option v-for="item in tableList" :key="item.table" :label="item.table_alias || item.table" :value="item.table" />
        </el-select>
      </el-form-item>-->

      <el-form-item v-show="!dataSrcVisible" :label="$t('dataSource.table')+':'">
        <span style="font-size: 12px;margin-right: 5px;">{{ selectedDBName }}/{{ selectedTableName }}</span>
        <el-button type="text" size="mini" @click="editDataSrc">
          {{ $t('common.edit') }}
        </el-button>
      </el-form-item>
      <el-form-item :label="$t('dataSource.fields')+':'">
        <draggable v-model="tableSchema" v-loading="schemaLoading" :group="{name: 'col',pull: 'clone', put: false}" :move="handleMove">
          <div v-for="col in tableSchema" :key="col.Column" class="drag-list-item">
            <i class="el-icon-s-unfold" style="font-size: 12px;color:#409EFF;" />
            {{ col.Column }}
          </div>
        </draggable>
      </el-form-item>
    </el-form>
  </div>
</template>
<script>
//import { sourceList, linkedTablesByBase } from '@/api/source'
import * as dsQueryApi from '@/api/datasync/metadataquery'
import draggable from 'vuedraggable'
//import exeSql from '@/api/exeSql'

export default {
  components: { draggable },
  props: {
    resultLoading: {
      default: false
    },
    dataSrc: {
      requred: true
    }
  },
  data() {
    return {
      schemaLoading: false,
      baseList: [],
      tableList: [],
      selectedDB:undefined,
      tableSchema: undefined,
      dataSrcVisible: this.$route.params.id === 'create',
      existWarning: null,
      querySql:undefined,
      modeName:undefined,
      modeId:undefined,
      datasourceId:undefined,
      dbType:undefined,
      listQuery:{
          current:1,
          size:10000
      }
    }
  },
  computed: {
    caculCols() {
      return this.$store.state.chart.caculCols
    },
    dimensions() {
      return this.$store.state.chart.dimensions
    },
    allSelected() {
      return this.dimensions.concat(this.caculCols)
    },
    selectedDBName() {
      const selectedDB = this.baseList.find(item => item.id === this.selectedDB)
      if (!selectedDB) return ''
      return selectedDB.modeName
    },
    selectedTableName() {
      const selectedTable = this.tableList.find(item => item.id === this.modeId)
      if (!selectedTable) return ''
        return selectedTable.modeName
    }
  },
  created() {
    this.getTableList()
  },
  methods: {
    async getTableList() {
      const { data } = await dsQueryApi.listSubcribes(this.listQuery)
      this.tableList = data.rows
    },
    initWithDataSrc(dataSrc) {
      if (dataSrc) {
        this.modeId = dataSrc.mode_id
        this.datasourceId = dataSrc.source_id
        this.querySql = dataSrc.query_sql
        this.dbType = dataSrc.db_type
        this.fetchSchema()
      } else {
        this.modeId = undefined
        this.datasourceId = undefined
        this.dbType = undefined
        this.tableSchema = []
        this.dataSrcVisible = true
      }
    },
    editDataSrc() {
      this.dataSrcVisible = true
      this.mode_id = undefined
      this.datasourceId = undefined
      this.dbType = undefined
    },
    handleTableChange(e) {
      this.tableList.find((item) => {
          if (item.id === e) {
             this.querySql = item.querySql
             this.datasourceId = item.datasourceId
             this.modeId = item.id
          }
      })
        this.dataSrcVisible = false
        this.fetchSchema()
        this.$store.commit('chart/SET_ALL_COLS', [])
        this.$emit('change', {
            query_sql: this.querySql,
            source_id: this.datasourceId,
            mode_id:this.modeId
        })
    },
    handleDataSrcChange() {
      this.dataSrcVisible = false
      this.fetchSchema()
      this.$store.commit('chart/SET_ALL_COLS', [])
      this.$emit('change', {
          query_sql: this.querySql,
          source_id: this.datasourceId,
          mode_id:this.modeId
      })
    },

    fetchSchema() {
      this.schemaLoading = true
      dsQueryApi.getColumnsByQuerySql_V3({marketId:this.modeId}).then(resp => {
        this.schemaLoading = false
        this.tableSchema = resp.data.map((item, index) => {
          return {
            Column: item.fieldName,
            Type: item.fieldTypeName,
            id: index
          }
        })
        this.$store.commit('chart/SET_ALL_COLS', this.tableSchema)
      })
    },
    handleMove(evt, originalEvent) {
      if (this.allSelected.find(item => item.Column === evt.draggedContext.element.Column)) {
        if (!this.existWarning) {
          this.existWarning = this.$message({
            type: 'warning',
            message: this.$t('chart.fieldExisted'),
            onClose: () => {
              this.existWarning = null
            }
          })
        }
        return false
      }
    }
  }
}
</script>
<style lang="scss" scoped>
.panel {
  ::v-deep .el-form-item__label {
     line-height: initial;
   }
  ::v-deep  .el-form-item__content {
    line-height: initial;
  }
}
.drag-list-item {
  line-height: 1.5;
  font-size: 14px;
  color:#606266;
  cursor: -webkit-grab;
}
</style>
