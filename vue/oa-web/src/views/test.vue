<template>
  <div>
    <div>账号切换</div>
    <button @click="noob()" type="default" size="mini">noob</button>
    <button @click="highping()" type="default" size="mini">highping</button>
    <button @click="admin()" type="default" size="mini">admin</button>
    <div>当前token：{{ token }}</div>
  </div>
</template>

<script>
export default {
  name: "Test",

  data() {
    return {
      token: ''
    };
  },

  created(){
    this.token = window.localStorage.getItem('token')
  },

  methods: {
    admin() {
      window.localStorage.setItem('token', '');
      let token = 'eyJhbGciOiJIUzUxMiIsInppcCI6IkdaSVAifQ.H4sIAAAAAAAAAKtWKi5NUrJScgwN8dANDXYNUtJRSq0oULIyNDcwMzW0sDQ11FEqLU4t8kwBikGYeYm5qUAtiSm5mXlKtQADZEv_QgAAAA.HyWfch-DlOSuUZfxY4ib1BGQozdFQ4r5YOXZPnAq4YHQr9nwGVX3FRmSVaBbcMgtY5mUFxrKO7TyGeK0WMpo6g'
      window.localStorage.setItem('token', token);
      this.token = window.localStorage.getItem('token')
    },

    noob() {
      window.localStorage.setItem('token', '');
      let token = 'eyJhbGciOiJIUzUxMiIsInppcCI6IkdaSVAifQ.H4sIAAAAAAAAAKtWKi5NUrJScgwN8dANDXYNUtJRSq0oULIyNDcwMzW0sDA31lEqLU4t8kwBiUHYeYm5qUA9efn5SUq1APLS1uJCAAAA.npDO425Zb72ftgFpEY1e1A2roubSPR398639TPXn3Ph6pxaXF0TgaAdOfFylQxHL2nPRK-wY8eStW0y8J2QHMw'
      window.localStorage.setItem('token', token);
      this.token = window.localStorage.getItem('token')
    },

    highping() {
      window.localStorage.setItem('token', '');
      let token = 'eyJhbGciOiJIUzUxMiIsInppcCI6IkdaSVAifQ.H4sIAAAAAAAAAKtWKi5NUrJScgwN8dANDXYNUtJRSq0oULIyNDcwMzW0sDSy0FEqLU4t8kwBikHZeYm5qUA9GZnpGQWZeelKtQBEv-5MRgAAAA.fQIQZWTw0m2K4UMMUR9OW-G7XZjeuy5R7nEDHDqGowOhAjl0sy5E443nFEhA4qMPuUC6M3dAcoFDI2T_1QfqpA'
      window.localStorage.setItem('token', token);
      this.token = window.localStorage.getItem('token')
    }
  }
}
</script>
