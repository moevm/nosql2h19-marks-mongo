<template>
  <div>
    <b-navbar sticky variant="light" type="light">
      <b-navbar-nav>
        <router-link to="/" v-slot="{ href, navigate }">
          <b-navbar-brand :href="href" @click="navigate">
            Marks
          </b-navbar-brand>
        </router-link>


        <b-nav-dropdown text="Search">
          <router-link to="/students" v-slot="{ href, navigate }">
            <b-dropdown-item :href="href" @click="navigate">
              Students
            </b-dropdown-item>
          </router-link>
          <router-link to="/groups" v-slot="{ href, navigate }">
            <b-dropdown-item :href="href" @click="navigate">
              Groups
            </b-dropdown-item>
          </router-link>
          <router-link to="/faculties" v-slot="{ href, navigate }">
            <b-dropdown-item :href="href" @click="navigate">
              Faculties
            </b-dropdown-item>
          </router-link>
          <router-link to="/departments" v-slot="{ href, navigate }">
            <b-dropdown-item :href="href" @click="navigate">
              Departments
            </b-dropdown-item>
          </router-link>
          <router-link to="/courses" v-slot="{ href, navigate }">
            <b-dropdown-item :href="href" @click="navigate">
              Courses
            </b-dropdown-item>
          </router-link>
          <router-link to="/semesters" v-slot="{ href, navigate }">
            <b-dropdown-item :href="href" @click="navigate">
              Semesters
            </b-dropdown-item>
          </router-link>
        </b-nav-dropdown>
        <router-link to="/stats" v-slot="{ href, navigate }">
          <b-nav-item :href="href" @click="navigate">
            Statistics
          </b-nav-item>
        </router-link>
      </b-navbar-nav>
    </b-navbar>
  </div>
</template>

<script>
export default {
  name: 'NavigationBar'
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
h3 {
  margin: 40px 0 0;
}
ul {
  list-style-type: none;
  padding: 0;
}
li {
  display: inline-block;
  margin: 0 10px;
}
a {
  color: #42b983;
}
</style>
