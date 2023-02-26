<template>
  <div class="sidebar-logo-container" :class="{'collapse':collapse}" :style="{ backgroundColor: sideTheme === 'theme-dark' ? variables.menuBg : variables.menuLightBg }">
    <transition name="sidebarLogoFade">
      <router-link v-if="collapse" key="collapse" class="sidebar-logo-link" to="/">
        <img v-if="logo" :src="logo" class="sidebar-logo">
        <!--<h1 v-else class="sidebar-title" :style="{ color: sideTheme === 'theme-dark' ? variables.sidebarTitle : variables.sidebarLightTitle }">{{ title }} </h1>-->
      </router-link>
      <router-link v-else key="expand" class="sidebar-logo-link" to="/">
        <img v-if="logo" :src="logo" class="sidebar-logo">
        <h1 class="sidebar-title" :style="{ color: sideTheme === 'theme-dark' ? variables.sidebarTitle : variables.sidebarLightTitle }">{{ title1 }} <br /> {{ title2 }} </h1>
      </router-link>
    </transition>
  </div>
</template>

<script>
import logoImg from '@/assets/logo/logo.png'
import variables from '@/assets/styles/variables.scss'

export default {
  name: 'SidebarLogo',
  props: {
    collapse: {
      type: Boolean,
      required: true
    }
  },
  computed: {
    variables() {
      return variables;
    },
	sideTheme() {
      return this.$store.state.settings.sideTheme
    }
  },
  data() {
    return {
      title1: 'AMDATA',
      title2: '数据管理平台',
      logo: logoImg
    }
  },
  beforeCreate(){
    this.getConfigKey("sys.app.title1").then(response => {
      this.title1 = response.msg;
    });
    this.getConfigKey("sys.app.title2").then(response => {
      this.title2 = response.msg;
    });
  }
}
</script>

<style lang="scss" scoped>

.sidebarLogoFade-enter,
.sidebarLogoFade-leave-to {
  opacity: 0;
}

.sidebar-logo-container {
  position: relative;
  width: 100%;
  height: 50px;
  line-height: 50px;
  background: #FF0000;
  text-align: center;
  overflow: hidden;

  & .sidebar-logo-link {
    height: 100%;
    width: 100%;

    & .sidebar-logo {
      //width: 100%;
      //vertical-align: middle;
      //image-rendering: -moz-crisp-edges; /* Firefox */
      //image-rendering: -o-crisp-edges; /* Opera */
      //image-rendering: -webkit-optimize-contrast; /*Webkit (non-standard naming) */
      //image-rendering: crisp-edges;
      //-ms-interpolation-mode: nearest-neighbor;
      width: 32px;
      height: 32px;
      vertical-align: middle;
      margin-right: 5px;
    }

    & .sidebar-title {
      display: inline-block;
      //margin: 0;
      color: #2b2f3a;
      width: 100px;
      //height:100%;
      font-weight: 600;
      line-height: 18px;
      font-size: 16px;
      font-family: Avenir, Helvetica Neue, Arial, Helvetica, sans-serif;
      vertical-align: middle;
      margin-right: 10px;
    }
  }

  &.collapse {
    //height: 50px;
    .sidebar-logo {
      width: 60%;
      height: 60%;
      margin: 0px;
    }
  }
}
</style>
