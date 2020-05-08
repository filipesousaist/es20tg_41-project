<template>
  <v-dialog
    v-model="dialog"
    @keydown.esc="closePermissionsDialog"
    max-width="40%"
    persistent
  >
    <v-card max-height="50%">
      <v-card-title>
        <span class="headline">Edit dashboard stats permissions</span>
      </v-card-title>

      <v-switch
        class="ma-4"
        v-model="editDashboardPermissions.showNumProposedQuestions"
        label="Show number of proposed questions"
      />

      <v-switch
        class="ma-4"
        v-model="editDashboardPermissions.showNumAcceptedQuestions"
        label="Show number of accepted questions"
      />
      <v-switch
        class="ma-4"
        v-model="editDashboardPermissions.showTotalTournaments"
        label="Show number of tournaments participated"
      />
      <v-switch
        class="ma-4"
        v-model="editDashboardPermissions.showHighestResult"
        label="Show highest result tournament"
      />

      <v-card-actions>
        <v-btn dark color="red" @click="closePermissionsDialog">Close</v-btn>
        <v-spacer />
        <v-btn dark color="green" @click="updatePermissions">Save</v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script lang="ts">
import { Component, Vue, Prop } from 'vue-property-decorator';
import DashboardPermissions from '@/models/dashboard/DashboardPermissions';

@Component
export default class DashboardPermissionsDialog extends Vue {
  @Prop({ type: DashboardPermissions, required: true })
  readonly dashboardPermissions!: DashboardPermissions;

  @Prop({ type: Boolean, required: true }) readonly dialog!: boolean;

  editDashboardPermissions!: DashboardPermissions;

  created() {
    this.editDashboardPermissions = new DashboardPermissions(
      this.dashboardPermissions
    );
  }

  closePermissionsDialog() {
    this.$emit('close-permissions-dialog');
  }

  updatePermissions() {
    this.$emit('update-permissions', this.editDashboardPermissions);
  }
}
</script>
