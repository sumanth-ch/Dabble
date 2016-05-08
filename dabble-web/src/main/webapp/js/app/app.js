/**
 * 
 */

var app = angular.module("", []);
app.controller("UserController", function($scope, $http) {
	$scope.path = "/rest/v0/service/user/";

	$http.get()

});