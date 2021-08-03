<template>
  <div class="sidebar-logo-container" :class="{'collapse':collapse}" :style="{ backgroundColor: sideTheme === 'theme-dark' ? variables.menuBg : variables.menuLightBg }">
    <transition name="sidebarLogoFade">
      <router-link v-if="collapse" key="collapse" class="sidebar-logo-link" to="/">
        <img v-if="logo_collapse" :src="logo_collapse" class="sidebar-logo">
        <!--<h1 v-else class="sidebar-title" :style="{ color: sideTheme === 'theme-dark' ? variables.sidebarTitle : variables.sidebarLightTitle }">{{ title }} </h1>-->
      </router-link>
      <router-link v-else key="expand" class="sidebar-logo-link" to="/">
        <img v-if="logo" :src="logo" class="sidebar-logo">
        <!--<h1 class="sidebar-title" :style="{ color: sideTheme === 'theme-dark' ? variables.sidebarTitle : variables.sidebarLightTitle }">{{ title }} </h1>-->
      </router-link>
    </transition>
  </div>
</template>

<script>
import logoImg from '@/assets/logo/logo.png'
import logoImg_collapse from '@/assets/logo/logo_collapse.png'
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
      title: 'LinkBI 灵柏',
      logo: logoImg,
	    logo_collapse: logoImg_collapse
    }
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
  //height: 84px;
  //line-height: 50px;
  background: #2b2f3a;
  text-align: center;
  overflow: hidden;

  & .sidebar-logo-link {
    height: 100%;
    width: 100%;

    & .sidebar-logo {
      width: 100%;
      //height: 32px;
      vertical-align: middle;
      image-rendering: -moz-crisp-edges; /* Firefox */
      image-rendering: -o-crisp-edges; /* Opera */
      image-rendering: -webkit-optimize-contrast; /*Webkit (non-standard naming) */
      image-rendering: crisp-edges;
      -ms-interpolation-mode: nearest-neighbor;
      //margin-left: 5px;
      //margin-top: 10px;
      //margin-right: 5px;
    }

    & .sidebar-title {
      //display: inline-block;
      //margin: 0;
      color: #ffffff;
      font-weight: 600;
      //line-height: 50px;
      font-size: 16px;
      font-family: Avenir, Helvetica Neue, Arial, Helvetica, sans-serif;
      vertical-align: middle;
    }
  }

  &.collapse {
    height: 50px;
    .sidebar-logo {
      width: 90%;
      height: 90%;
      margin: 0px;
    }
  }
}
</style>
