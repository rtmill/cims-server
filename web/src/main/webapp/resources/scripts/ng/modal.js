'use strict';

angular.module('tabletuing.modal', [])
	.factory('openModal', ['$modal', function($modal) {
		return  function (title, items) {
		    var modalInstance = $modal.open({
		        templateUrl: 'partials/modalContent.html',
		        controller: 'ModalInstanceCtrl',
		        resolve: {
		        	title: function () {
		        		return title;
		        	},
		            items: function () {
		            	return items;
		            }
		        }
		             
		      });
		    };
	}]);

