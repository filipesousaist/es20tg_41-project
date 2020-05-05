<template>
	<div class="container">
		<h2>Available Tournaments in {{ course }}</h2>
		<ul>
			<li class="list-header">
				<div class="col">Name</div>
				<div class="col">Topics</div>
				<div class="col">Available since</div>
				<div class="col">Available until</div>
				<div class="col">Questions</div>
				<div class="col">Participants</div>
				<div class="col last-col"></div>
			</li>
			<li
				class="list-row"
				v-for="tournament in tournaments"
				:key="tournament.id"
			>
				<div class="col">
					{{ tournament.name }}
				</div>
				<div class="col">
					<div
						v-for="topic in tournament.topics"
						:key="topic.id"
						style="padding-bottom:2px;padding-top: 2px;"
					>
						<v-chip>{{ topic.name }}</v-chip>
					</div>
				</div>
				<div class="col">
					{{ tournament.beginningTime }}
				</div>
				<div class="col">
					{{ tournament.endingTime }}
				</div>
				<div class="col">
					{{ tournament.numberOfQuestions }}
				</div>
				<div class="col">
					{{ tournament.studentsUsername.length }}
				</div>
				<div class="col last-col">
					<v-btn
						color="primary"
						@click="answerQuiz(tournament)"
						v-on="on"
						data-cy="answer"
					>
						Answer
					</v-btn>
				</div>
			</li>
		</ul>
	</div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import Tournament from '@/models/tournament/Tournament';
import RemoteServices from '@/services/RemoteServices';
import StatementManager from '@/models/statement/StatementManager';

@Component

export default class ParticipateTournament extends Vue {
	course = this.$store.getters.getCurrentCourse.name;
	username = this.$store.getters.getUser.username;
	tournaments: Tournament[] = [];

	async created() {
		await this.$store.dispatch('loading');
		try {
			this.tournaments = (
				await RemoteServices.getAllOpenTournament()
			).reverse();
		} catch (error) {
			await this.$store.dispatch('error', error);
		}
		await this.$store.dispatch('clearLoading');
	}

	async answerQuiz(tournament: Tournament) {
		if (tournament.quiz == null) {
      await this.$store.dispatch(
        'error',
        'Error: Tournament does not have a quiz associated'
      );
      return;
		}
		else {
      let statementManager: StatementManager = StatementManager.getInstance;
      statementManager.statementQuiz = tournament.quiz;
      await this.$router.push({ name: 'solve-quiz' });
    }
		await this.$store.dispatch('loading');
	}
}


</script>

<style lang="scss" scoped>
	.container {
		max-width: 1250px;
		margin-left: auto;
		margin-right: auto;
		padding-left: 10px;
		padding-right: 10px;

		h2 {
			font-size: 26px;
			margin: 20px 0;
			text-align: center;

			small {
				font-size: 0.5em;
			}
		}

		ul {
			overflow: hidden;
			padding: 0 5px;

			li {
				border-radius: 3px;
				padding: 15px 10px;
				display: flex;
				justify-content: space-between;
				margin-bottom: 10px;
			}

			.list-header {
				background-color: #1976d2;
				color: white;
				font-size: 14px;
				text-transform: uppercase;
				letter-spacing: 0.03em;
				text-align: center;
			}

			.col {
				flex-basis: 25% !important;
				margin: auto; /* Important */
				text-align: center;
			}

			.list-row {
				background-color: #ffffff;
				box-shadow: 0 0 9px 0 rgba(0, 0, 0, 0.1);
				display: flex;
			}
		}
	}
</style>
