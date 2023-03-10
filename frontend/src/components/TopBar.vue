<template>
  <nav>
    <v-app-bar color="primary" clipped-left>
      <v-app-bar-nav-icon
        @click.stop="drawer = !drawer"
        class="hidden-md-and-up"
        aria-label="Menu"
      />

      <v-toolbar-title>
        <v-btn
          dark
          active-class="no-active"
          text
          tile
          to="/"
          v-if="currentCourse"
        >
          {{ currentCourse.name }}
        </v-btn>
        <v-btn dark active-class="no-active" text tile to="/" v-else>
          {{ appName }}
        </v-btn>
      </v-toolbar-title>

      <v-spacer />

      <v-toolbar-items class="hidden-sm-and-down" hide-details>
        <v-menu offset-y v-if="isAdmin" open-on-hover>
          <template v-slot:activator="{ on }">
            <v-btn v-on="on" text dark data-cy="administrationMenuButton">
              Administration
              <v-icon>fas fa-file-alt</v-icon>
            </v-btn>
          </template>
          <v-list dense>
            <v-list-item to="/admin/courses" data-cy="manageCoursesMenuButton">
              <v-list-item-action>
                <v-icon>fas fa-school</v-icon>
              </v-list-item-action>
              <v-list-item-content>
                <v-list-item-title>Manage Courses</v-list-item-title>
              </v-list-item-content>
            </v-list-item>
          </v-list>
        </v-menu>

        <v-menu offset-y v-if="isTeacher && currentCourse" open-on-hover>
          <template v-slot:activator="{ on }">
            <v-btn v-on="on" text dark>
              Management
              <v-icon>fas fa-file-alt</v-icon>
            </v-btn>
          </template>
          <v-list dense>
            <v-list-item to="/management/questions">
              <v-list-item-action>
                <v-icon>question_answer</v-icon>
              </v-list-item-action>
              <v-list-item-content>
                <v-list-item-title>Questions</v-list-item-title>
              </v-list-item-content>
            </v-list-item>
            <v-list-item to="/management/topics">
              <v-list-item-action>
                <v-icon>category</v-icon>
              </v-list-item-action>
              <v-list-item-content>
                <v-list-item-title>Topics</v-list-item-title>
              </v-list-item-content>
            </v-list-item>
            <v-list-item to="/management/quizzes">
              <v-list-item-action>
                <v-icon>ballot</v-icon>
              </v-list-item-action>
              <v-list-item-content>
                <v-list-item-title>Quizzes</v-list-item-title>
              </v-list-item-content>
            </v-list-item>
            <v-list-item to="/management/assessments">
              <v-list-item-action>
                <v-icon>book</v-icon>
              </v-list-item-action>
              <v-list-item-content>
                <v-list-item-title>Assessments</v-list-item-title>
              </v-list-item-content>
            </v-list-item>
            <v-list-item to="/management/students">
              <v-list-item-action>
                <v-icon>school</v-icon>
              </v-list-item-action>
              <v-list-item-content>
                <v-list-item-title>Students</v-list-item-title>
              </v-list-item-content>
            </v-list-item>
            <v-list-item to="/management/proposed-questions">
              <v-list-item-action>
                <v-icon>live_help</v-icon>
              </v-list-item-action>
              <v-list-item-content>
                <v-list-item-title>Proposed Questions</v-list-item-title>
              </v-list-item-content>
            </v-list-item>
            <v-list-item to="/management/impexp">
              <v-list-item-action>
                <v-icon>cloud</v-icon>
              </v-list-item-action>
              <v-list-item-content>
                <v-list-item-title>ImpExp</v-list-item-title>
              </v-list-item-content>
            </v-list-item>
          </v-list>
        </v-menu>

        <v-btn
          to="/student/studentAnsweredQuestions/"
          v-if="isStudent && currentCourse"
          text
          dark
        >
          Answered Questions
          <v-icon>fas fa-file-alt</v-icon>
        </v-btn>


        <v-btn
          to="/student/studentQuestions/"
          v-if="isStudent && currentCourse"
          text
          dark
        >
          Student Questions
          <v-icon>fas fa-file-alt</v-icon>
        </v-btn>

        <v-menu offset-y v-if="isStudent && currentCourse" open-on-hover>
          <template v-slot:activator="{ on }">
            <v-btn v-on="on" text dark data-cy="QuizzesButton">
              Quizzes
              <v-icon>fas fa-file-alt</v-icon>
            </v-btn>
          </template>
          <v-list dense>
            <v-list-item to="/student/available">
              <v-list-item-action>
                <v-icon>assignment</v-icon>
              </v-list-item-action>
              <v-list-item-content>
                <v-list-item-title>Available</v-list-item-title>
              </v-list-item-content>
            </v-list-item>
            <v-list-item to="/student/create">
              <v-list-item-action>
                <v-icon>create</v-icon>
              </v-list-item-action>
              <v-list-item-content>
                <v-list-item-title>Create</v-list-item-title>
              </v-list-item-content>
            </v-list-item>
            <v-list-item to="/student/scan">
              <v-list-item-action>
                <v-icon>fas fa-qrcode</v-icon>
              </v-list-item-action>
              <v-list-item-content>
                <v-list-item-title>Scan</v-list-item-title>
              </v-list-item-content>
            </v-list-item>
            <v-list-item to="/student/solved">
              <v-list-item-action>
                <v-icon>done</v-icon>
              </v-list-item-action>
              <v-list-item-content>
                <v-list-item-title>Solved</v-list-item-title>
              </v-list-item-content>
            </v-list-item>
          </v-list>
        </v-menu>

        <v-menu offset-y v-if="isStudent && currentCourse" open-on-hover>
          <template v-slot:activator="{ on }">
            <v-btn v-on="on" text dark>
              Tournaments
              <v-icon>fas fa-trophy</v-icon>
            </v-btn>
          </template>
          <v-list dense>
            <v-list-item to="/student/createTournament">
              <v-list-item-action>
                <v-icon>create</v-icon>
              </v-list-item-action>
              <v-list-item-content>
                <v-list-item-title>Create</v-list-item-title>
              </v-list-item-content>
            </v-list-item>
            <v-list-item to="/student/enrollTournament">
              <v-list-item-action>
                <v-icon>fas fa-user-check</v-icon>
              </v-list-item-action>
              <v-list-item-content>
                <v-list-item-title>Enroll</v-list-item-title>
              </v-list-item-content>
            </v-list-item>
            <v-list-item to="/student/participateTournament">
              <v-list-item-action>
                <v-icon>fas fa-marker</v-icon>
              </v-list-item-action>
              <v-list-item-content>
                <v-list-item-title>Participate</v-list-item-title>
              </v-list-item-content>
            </v-list-item>
            <v-list-item to="/student/showTournaments">
              <v-list-item-action>
                <v-icon>fas fa-list-ul</v-icon>
              </v-list-item-action>
              <v-list-item-content>
                <v-list-item-title>Show</v-list-item-title>
              </v-list-item-content>
            </v-list-item>
          </v-list>
        </v-menu>

        <v-menu offset-y v-if="isStudent && currentCourse" open-on-hover>
          <template v-slot:activator="{ on }">
            <v-btn v-on="on" text dark>
              User
              <v-icon>fas fa-user</v-icon>
            </v-btn>
          </template>
          <v-list dense>
            <v-list-item to="/student/stats">
              <v-list-item-action>
                <v-icon>bar_chart</v-icon>
              </v-list-item-action>
              <v-list-item-content>
                <v-list-item-title>Stats</v-list-item-title>
              </v-list-item-content>
            </v-list-item>
            <v-list-item to="/student/dashboard">
              <v-list-item-action>
                <v-icon>view_list</v-icon>
              </v-list-item-action>
              <v-list-item-content>
                <v-list-item-title>Dashboard</v-list-item-title>
              </v-list-item-content>
            </v-list-item>
          </v-list>
        </v-menu>

        <v-btn
          v-if="isLoggedIn && moreThanOneCourse"
          to="/courses"
          active-class="no-active"
          text
          dark
        >
          Change course
          <v-icon>fa fa-book</v-icon>
        </v-btn>

        <v-btn
          v-if="isLoggedIn"
          @click="logout"
          data-cy="logoutButton"
          text
          dark
        >
          Logout
          <v-icon>fas fa-sign-out-alt</v-icon>
        </v-btn>

        <v-btn v-else :href="fenixUrl" text dark>
          Login <v-icon>fas fa-sign-in-alt</v-icon>
        </v-btn>
      </v-toolbar-items>
    </v-app-bar>

    <!-- Start of mobile side menu -->
    <v-navigation-drawer app v-model="drawer" absolute dark temporary>
      <v-toolbar flat>
        <v-list>
          <v-list-item>
            <v-list-item-title class="title">Menu</v-list-item-title>
          </v-list-item>
        </v-list>
      </v-toolbar>

      <v-list class="pt-0" dense>
        <!-- Administration Group-->
        <v-list-group
          prepend-icon="fas fa-file-alt"
          :value="false"
          v-if="isAdmin"
        >
          <template v-slot:activator>
            <v-list-item-title>Administration</v-list-item-title>
          </template>
          <v-list-item to="/admin/courses">
            <v-list-item-action>
              <v-icon>fas fa-school</v-icon>
            </v-list-item-action>
            <v-list-item-content>
              <v-list-item-title>Manage Courses</v-list-item-title>
            </v-list-item-content>
          </v-list-item>
        </v-list-group>

        <!-- Management Group-->
        <v-list-group
          prepend-icon="fas fa-file-alt"
          :value="false"
          v-if="isTeacher && currentCourse"
        >
          <template v-slot:activator>
            <v-list-item-title>Management</v-list-item-title>
          </template>
          <v-list-item to="/management/questions">
            <v-list-item-action>
              <v-icon>question_answer</v-icon>
            </v-list-item-action>
            <v-list-item-content>
              <v-list-item-title>Questions</v-list-item-title>
            </v-list-item-content>
          </v-list-item>
          <v-list-item to="/management/topics">
            <v-list-item-action>
              <v-icon>category</v-icon>
            </v-list-item-action>
            <v-list-item-content>
              <v-list-item-title>Topics</v-list-item-title>
            </v-list-item-content>
          </v-list-item>
          <v-list-item to="/management/quizzes">
            <v-list-item-action>
              <v-icon>ballot</v-icon>
            </v-list-item-action>
            <v-list-item-content>
              <v-list-item-title>Quizzes</v-list-item-title>
            </v-list-item-content>
          </v-list-item>
          <v-list-item to="/management/assessments">
            <v-list-item-action>
              <v-icon>book</v-icon>
            </v-list-item-action>
            <v-list-item-content>
              <v-list-item-title>Assessments</v-list-item-title>
            </v-list-item-content>
          </v-list-item>
          <v-list-item to="/management/students">
            <v-list-item-action>
              <v-icon>school</v-icon>
            </v-list-item-action>
            <v-list-item-content>
              <v-list-item-title>Students</v-list-item-title>
            </v-list-item-content>
          </v-list-item>
          <v-list-item to="/management/proposed-questions">
            <v-list-item-action>
              <v-icon>live_help</v-icon>
            </v-list-item-action>
            <v-list-item-content>
              <v-list-item-title>Proposed Questions</v-list-item-title>
            </v-list-item-content>
          </v-list-item>
          <v-list-item to="/management/impexp">
            <v-list-item-action>
              <v-icon>cloud</v-icon>
            </v-list-item-action>
            <v-list-item-content>
              <v-list-item-title>ImpExp</v-list-item-title>
            </v-list-item-content>
          </v-list-item>
        </v-list-group>

        <!-- Student Group-->
        <v-list-group
          prepend-icon="account_circle"
          :value="false"
          v-if="isStudent && currentCourse"
        >
          <template v-slot:activator>
            <v-list-item-title>Student</v-list-item-title>
          </template>

          <v-list-item
            to="/student/available"
            v-if="isStudent && currentCourse"
          >
            <v-list-item-action>
              <v-icon>assignment</v-icon>
            </v-list-item-action>
            <v-list-item-content>Available Quizzes</v-list-item-content>
          </v-list-item>

          <v-list-item to="/student/studentQuestions">
            <v-list-item-action>
              <v-icon>assignment</v-icon>
            </v-list-item-action>
            <v-list-item-content>Student Questions</v-list-item-content>
          </v-list-item>

          <v-list-item to="/student/create">
            <v-list-item-action>
              <v-icon>create</v-icon>
            </v-list-item-action>
            <v-list-item-content>Create Quiz</v-list-item-content>
          </v-list-item>

          <v-list-item to="/student/scan">
            <v-list-item-action>
              <v-icon>fas fa-qrcode</v-icon>
            </v-list-item-action>
            <v-list-item-content>Scan</v-list-item-content>
          </v-list-item>

          <v-list-item to="/student/solved">
            <v-list-item-action>
              <v-icon>done</v-icon>
            </v-list-item-action>
            <v-list-item-content>Solved Quizzes</v-list-item-content>
          </v-list-item>

          <v-list-item to="/student/stats">
            <v-list-item-action>
              <v-icon>bar_chart</v-icon>
            </v-list-item-action>
            <v-list-item-content>Stats</v-list-item-content>
          </v-list-item>

          <v-list-item to="/student/dashboard">
            <v-list-item-action>
              <v-icon>view_list</v-icon>
            </v-list-item-action>
            <v-list-item-content>Dashboard</v-list-item-content>
          </v-list-item>

          <v-list-item to="/student/createTournament">
            <v-list-item-action>
              <v-icon>create</v-icon>
            </v-list-item-action>
            <v-list-item-content>Create Tournament</v-list-item-content>
          </v-list-item>

          <v-list-item to="/student/enrollTournament">
            <v-list-item-action>
              <v-icon>fas fa-user-check</v-icon>
            </v-list-item-action>
            <v-list-item-content>Enroll Tournament</v-list-item-content>
          </v-list-item>

          <v-list-item to="/student/showTournaments">
            <v-list-item-action>
              <v-icon>fas fa-list-ul</v-icon>
            </v-list-item-action>
            <v-list-item-content>Show Tournaments</v-list-item-content>
          </v-list-item>
        </v-list-group>

        <v-list-item to="/courses" v-if="isLoggedIn && moreThanOneCourse">
          <v-list-item-action>
            <v-icon>fas fa-book</v-icon>
          </v-list-item-action>
          <v-list-item-content>Change course</v-list-item-content>
        </v-list-item>
        <v-list-item @click="logout" v-if="isLoggedIn">
          <v-list-item-action>
            <v-icon>fas fa-sign-out-alt</v-icon>
          </v-list-item-action>
          <v-list-item-content>Logout</v-list-item-content>
        </v-list-item>
        <v-list-item :href="fenixUrl" v-else>
          <v-list-item-action>
            <v-icon>fas fa-sign-in-alt</v-icon>
          </v-list-item-action>
          <v-list-item-content>Login</v-list-item-content>
        </v-list-item>
      </v-list>
    </v-navigation-drawer>
    <!-- End of mobile side menu -->
  </nav>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';

@Component
export default class TopBar extends Vue {
  fenixUrl: string = process.env.VUE_APP_FENIX_URL;
  appName: string = process.env.VUE_APP_NAME;
  drawer: boolean = false;

  get currentCourse() {
    return this.$store.getters.getCurrentCourse;
  }

  get moreThanOneCourse() {
    return (
      this.$store.getters.getUser.coursesNumber > 1 &&
      this.$store.getters.getCurrentCourse
    );
  }

  get isLoggedIn() {
    return this.$store.getters.isLoggedIn;
  }

  get isTeacher() {
    return this.$store.getters.isTeacher;
  }

  get isAdmin() {
    return this.$store.getters.isAdmin;
  }

  get isStudent() {
    return this.$store.getters.isStudent;
  }

  async logout() {
    await this.$store.dispatch('logout');
    await this.$router.push({ name: 'home' }).catch(() => {});
  }
}
</script>

<style lang="scss" scoped>
.no-active::before {
  opacity: 0 !important;
}

nav {
  z-index: 300;
}
</style>
