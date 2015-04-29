/**
 * Agile Config Controller
 */
angular
		.module('AgileApp')
		.controller(
				'TeamCtrl',
				[
						'$scope',
						'AgileFactory',
						'$resource',
						'$window',
						'$modal',
						'$filter',
						'$routeParams',
						'LayoutService',
						'PopupService',
						function($scope, AgileFactory, $resource, $window,
								$modal, $filter, $routeParams, LayoutService,
								PopupService) {

							// ////////////////////////////////////
							// Agile Team table
							// ////////////////////////////////////
							$scope.team = {};
							$scope.team.uuid = $routeParams.team;
							$scope.selectedTeam = {};
							$scope.selectedTeam.name = "";
							$scope.selectedTeam.backlog = "";
							$scope.selectedTeam.sprint = "";
							$scope.isLoaded = "";

							var openTeamTmpl = '<button class="btn btn-default btn-sm" ng-click="openTeam(row.entity)">Open</button>';
							var configTeamTmpl = '<button class="btn btn-default btn-sm" ng-click="configTeam(row.entity)">Config</button>';
							var openBacklogImpl = '<button class="btn btn-default btn-sm" ng-click="openBacklog(row.entity)">Backlog</button>';
							var openKanbanImpl = '<button class="btn btn-default btn-sm" ng-click="openKanban(row.entity)">Kanban</button>';

							$scope.teamGridOptions = {
								data : 'teams',
								enableHighlighting : true,
								enableColumnResize : true,
								multiSelect : false,
								showFilter : true,
								sortInfo : {
									fields : [ 'name' ],
									directions : [ 'asc' ]
								},
								columnDefs : [ {
									field : 'uuid',
									displayName : 'Id',
									width : 50
								}, {
									field : 'name',
									displayName : 'Name',
									width : 290
								}, {
									field : "backlog",
									displayName : 'Backlog',
									width : 66,
									cellTemplate : openBacklogImpl
								}, {
									field : "config",
									displayName : 'Config',
									width : 60,
									cellTemplate : configTeamTmpl
								} ]
							};

							$scope.openTeam = function(team) {
								window.location.assign("main#/team?team="
										.concat(team.uuid))
							}

							$scope.configTeam = function(team) {
								window.location.assign("main#/config?team="
										.concat(team.uuid))
							}

							$scope.openBacklog = function(team) {
								window.location.assign("main#/backlog?team="
										.concat(team.uuid))
							}

							$scope.refresh = function() {
								$scope.isLoaded = "";
								var loadingModal = PopupService
										.showLoadingModal();
								AgileFactory.getTeamSingle($scope.team).$promise
										.then(function(data) {
											$scope.selectedTeam = data;
											// $scope.updateSprints();
											// $scope.updateFeatureGroups();
											AgileFactory
													.getBacklog($scope.selectedTeam).$promise
													.then(function(data) {
														if (data && data.name) {
															$scope.selectedTeam.backlog = data.name;
															$scope.selectedTeam.backlogUuid = data.uuid;
														}
													});
											AgileFactory
													.getSprintCurrent($scope.selectedTeam).$promise
													.then(function(data) {
														if (data && data.name) {
															$scope.selectedTeam.sprint = data.name;
															$scope.selectedTeam.sprintUuid = data.uuid;
														}
													});
											// LayoutService
											// .resizeElementHeight("sprintConfigTable");
											// LayoutService
											// .resizeElementHeight("featureGroupConfigTable");
											// LayoutService.refresh();
											loadingModal.close();
											$scope.isLoaded = "true";
										});
							}

							$scope.refresh();

						} ]);
