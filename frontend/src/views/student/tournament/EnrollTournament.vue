<template>
  <v-card class="table">
    <v-data-table
      :headers="headers"
      :items="tournaments"
      :search="search"
      disable-pagination
      :hide-default-footer="true"
      :mobile-breakpoint="0"
      multi-sort
    >
    </v-data-table>
  </v-card>
</template>

<script>
export default {
  name: 'EnrollTournament'
};
</script>

<style scoped></style>
