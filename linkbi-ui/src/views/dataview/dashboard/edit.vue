<template>
  <div>
    <dbItem :dashboard="currentDashboard" mode="edit" />
  </div>
</template>
<script>
    import dbItem from './dbItem'
    import { getDashboardById } from '@/api/dataview/dashboard'

    export default {
        components: { dbItem },
        data() {
            return {
                currentDashboard: undefined
            }
        },
        created() {
            getDashboardById(this.$route.params.id).then(resp => {
                //resp.data.content = JSON.parse(resp.data.content)
                this.currentDashboard = resp.data
            })
        }
    }
</script>
