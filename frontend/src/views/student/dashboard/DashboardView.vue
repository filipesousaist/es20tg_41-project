<template>
  <v-card class="table">
    <v-data-table
      :headers="headers"
      :items="dashboardStatsList"
      :search="search"
      disable-pagination
      :hide-default-footer="true"
      multi-sort
      :mobile-breakpoint="0"
      :items-per-page="15"
      :footer-props="{ itemsPerPageOptions: [15, 30, 50, 100] }"
    >
      <template v-slot:top>
        <v-card-title>
          <v-text-field
            v-model="search"
            append-icon="search"
            label="Search"
            class="mx-2"
            data-cy="searchBar"
          />
          <v-spacer />
          <v-btn
            color="primary"
            dark
            @click="openPermissions"
            data-cy="permissionsButton"
          >
            <v-icon class="lock-icon">lock</v-icon>
            Permissions</v-btn
          >
        </v-card-title>
      </template>

      <template v-slot:item.username="{ item }">
        <v-chip :color="getColor(item.userId)" medium>
          <span>{{ item.username }}</span>
        </v-chip>
      </template>

      <template v-slot:item.name="{ item }">
        <v-chip :color="getColor(item.userId)" medium>
          <span>{{ item.name }}</span>
        </v-chip>
      </template>

      <template v-slot:item.numProposedQuestions="{ item }">
        <div data-cy="numProposedQuestions">
          {{
            item.numProposedQuestions === -1 ? '-' : item.numProposedQuestions
          }}
        </div>
      </template>

      <template v-slot:item.numAcceptedQuestions="{ item }">
        <div data-cy="numAcceptedQuestions">
          {{ item.numAcceptedQuestions === -1 ? '-' : item.numAcceptedQuestions}}
        </div>
      </template>
    </v-data-table>

    <permissions-dialog
      v-if="permissionsDialog"
      :dashboard-permissions="myPermissions"
      :dialog="permissionsDialog"
      v-on:update-permissions="updatePermissions"
      v-on:close-permissions-dialog="closePermissionsDialog"
    />
  </v-card>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import DashboardStats from '@/models/dashboard/DashboardStats';
import DashboardPermissions from '@/models/dashboard/DashboardPermissions';
import DashboardPermissionsDialog from '@/views/student/dashboard/DashboardPermissionsDialog.vue';

@Component({
  components: {
    'permissions-dialog': DashboardPermissionsDialog
  }
})
export default class DashboardView extends Vue {
  dashboardStatsList: DashboardStats[] = [];
  search: string = '';
  myId: number | null = null;
  permissionsDialog: boolean = false;
  myPermissions: DashboardPermissions | null = null;
  headers: object = [
    { text: 'Username', value: 'username', align: 'center' },
    {
      text: 'Name',
      value: 'name',
      align: 'center'
    },
    {
      text: 'Proposed questions',
      value: 'numProposedQuestions',
      align: 'center'
    },
    {
      text: 'Accepted questions',
      value: 'numAcceptedQuestions',
      align: 'center'
    }
    // TODO: add fields for each stat
  ];

  async created() {
    await this.$store.dispatch('loading');
    try {
      this.dashboardStatsList = await RemoteServices.getDashboardStats();
      this.markStudentEntry();
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  markStudentEntry() {
    let studentID: number = this.$store.getters.getUser.id;
    let studentStats: DashboardStats;
    for (let i: number = 0; i < this.dashboardStatsList.length; i++)
      if (this.dashboardStatsList[i].userId == studentID) {
        this.myId = studentID;
        studentStats = this.dashboardStatsList.splice(i, 1)[0];
        this.dashboardStatsList.unshift(studentStats);
        return;
      }
  }

  async openPermissions() {
    try {
      this.myPermissions = await RemoteServices.getDashboardPermissions();
      this.permissionsDialog = true;
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
  }

  async updatePermissions(dashboardPermissions: DashboardPermissions) {
    this.permissionsDialog = false;
    try {
      this.myPermissions = await RemoteServices.updateDashboardPermissions(
        dashboardPermissions
      );
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
  }

  closePermissionsDialog() {
    this.permissionsDialog = false;
  }

  getColor(studentId: number) {
    let user = this.$store.getters.getUser;
    if (user) return user.id == studentId ? 'green' : 'blue';
    return 'blue';
  }
}
</script>

<style>
.lock-icon {
  padding: 0 10px 0 0;
}
</style>
