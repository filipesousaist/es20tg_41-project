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
    </v-data-table>
  </v-card>
</template>

<script lang="ts">
import { Component, Vue, Watch } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import DashboardStats from '@/models/dashboard/DashboardStats';

@Component({
  components: {}
})
export default class DashboardView extends Vue {
  dashboardStatsList: DashboardStats[] = [];
  search: string = '';
  myId: number | null = null;
  headers: object = [
    { text: 'Username', value: 'username', align: 'center' },
    {
      text: 'Name',
      value: 'name',
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

  getColor(studentId: number) {
    return this.$store.getters.getUser.id == studentId ? 'green' : 'blue';
  }
}
</script>

<style scoped></style>
