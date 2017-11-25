"use strict";
const app = angular.module('demoAppModule', ['ui.bootstrap']);
app.config(['$qProvider', function ($qProvider) {
    $qProvider.errorOnUnhandledRejections(false);
}]);

app.controller('AgreementController', function($http, $location, $uibModal) {
    const demoApp = this;

    // We identify the node.
    const apiBaseURL = "/api/template/";
    let peers = [];
	let agreements = [];

    $http.get(apiBaseURL + "getAgreements").then((response) => agreements = response.data.me);

    $http.get(apiBaseURL + "peers").then((response) => peers = response.data.peers);

    demoApp.openModal = () => {
        const modalInstance = $uibModal.open({
            templateUrl: 'initiateAgreementModal.html',
            controller: 'ModalInstanceCtrl',
            controllerAs: 'modalInstance',
            resolve: {
                apiBaseURL: () => apiBaseURL,
                peers: () => peers
            }
        });

        modalInstance.result.then(() => {}, () => {});
    };
	
	demoApp.viewAgreement = (agreement) => {
		$http.get(apiBaseURL + "getAgreement/"+agreement.agrementName)
		.then(function(response) {
			var newAgreement = response.data.state.data;
			const modalInstance = $uibModal.open({
				templateUrl: 'viewAgreementModal.html',
				controller: 'AgreementCtrl',
				controllerAs: 'AgreementCtrl',
				resolve: {
					apiBaseURL: () => apiBaseURL,
					agreement: () => newAgreement
				}
			});
			modalInstance.result.then(() => {}, () => {});
		});
    };

	demoApp.amendAgreement = (agreement) => {
        const modalInstance = $uibModal.open({
            templateUrl: 'amendAgreementModal.html',
            controller: 'AmendAgreementCtrl',
            controllerAs: 'AmendAgreementCtrl',
            resolve: {
                apiBaseURL: () => apiBaseURL,
                agreement: () => agreement
            }
        });

        modalInstance.result.then(() => {}, () => {});
    };


	demoApp.viewAudit = (agreement) => {
        $http.get(apiBaseURL + "audit?agreementName=" + agreement.agrementName)
        .then(function(response) {
			demoApp.agreementAudits = response.data;
		});
    };

	demoApp.getAgreements = () => $http.get(apiBaseURL + "getAgreements")
        .then(function(response) {
			demoApp.agreements = response.data;
		});

    demoApp.getAgreements();
});

app.controller('ModalInstanceCtrl', function ($scope, $http, $location, $uibModalInstance, $uibModal, apiBaseURL, peers) {
    const modalInstance = this;

    modalInstance.peers = peers;
    modalInstance.form = {};
    modalInstance.agreement = {};
    modalInstance.formError = false;

    // Validate and create IOU.
    modalInstance.create = () => {
		console.log('Called Create');
        if (invalidFormInput()) {
            modalInstance.formError = true;
        } else {
            modalInstance.formError = false;

            $uibModalInstance.close();

            const createIOUEndpoint = apiBaseURL + "initFlow/" +modalInstance.form.counterparty;

            // Create PO and handle success / fail responses.
            $http.put(createIOUEndpoint, angular.toJson(modalInstance.form)).then(
                (result) => modalInstance.displayMessage(result),
                (result) => modalInstance.displayMessage(result)
            );
        }
    };

    modalInstance.displayMessage = (message) => {
        const modalInstanceTwo = $uibModal.open({
            templateUrl: 'messageContent.html',
            controller: 'messageCtrl',
            controllerAs: 'modalInstanceTwo',
            resolve: { message: () => message }
        });

        // No behaviour on close / dismiss.
        modalInstanceTwo.result.then(() => {}, () => {});
    };

    // Close create IOU modal dialogue.
    modalInstance.cancel = () => $uibModalInstance.dismiss();

    // Validate the IOU.
    function invalidFormInput() {
        return (modalInstance.form.counterparty === undefined);
    }
});

app.controller('AgreementCtrl', function ($scope, $http, $location, $uibModalInstance, $uibModal, apiBaseURL, agreement)
{
    const modalInstance = this;
	$scope.agreement = agreement;
    // Validate and create IOU.
    $scope.agree = (agreement) => {
		console.log('Called acceptFlow '+agreement);
		var updAgreement ={
			agrementName: agreement.agrementName,
			agreementValue: agreement.agreementValue,
			collateral: agreement.collateral
		}

		$uibModalInstance.close();

		const createIOUEndpoint = apiBaseURL + "acceptFlow";

		// Create PO and handle success / fail responses.
		$http.put(createIOUEndpoint, angular.toJson(updAgreement)).then(
			(result) => $scope.displayMessage(result),
			(result) => $scope.displayMessage(result)
		);
		//$scope.cancel();
    };

    $scope.displayMessage = (message) => {
        const modalInstanceTwo = $uibModal.open({
            templateUrl: 'messageContent.html',
            controller: 'messageCtrl',
            controllerAs: 'modalInstanceTwo',
            resolve: { message: () => message }
        });

        // No behaviour on close / dismiss.
        modalInstanceTwo.result.then(() => {}, () => {});
    };

    // Close create IOU modal dialogue.
    $scope.cancel = () => $uibModalInstance.dismiss();
});

app.controller('AmendAgreementCtrl', function ($scope, $http, $location, $uibModalInstance, $uibModal, apiBaseURL, agreement) {
    const modalInstance = this;
	$scope.agreement = agreement;
    // Validate and create IOU.
    $scope.amendAgreement = (agreement) => {
		console.log('Called Amend '+agreement);
		const updatedAgreement = {
			agrementName: agreement.agrementName,
			agreementValue: agreement.agreementValue,
			collateral: agreement.collateral
		};

		$uibModalInstance.close();
		const createIOUEndpoint = apiBaseURL + "amendFlow/" +$scope.agreement.cptyReciever;
		// Create PO and handle success / fail responses.
		$http.put(createIOUEndpoint, angular.toJson(updatedAgreement)).then(
			(result) => $scope.displayMessage(result),
			(result) => $scope.displayMessage(result)
		);
		//$scope.cancel();
    };

    $scope.displayMessage = (message) => {
        const modalInstanceTwo = $uibModal.open({
            templateUrl: 'messageContent.html',
            controller: 'messageCtrl',
            controllerAs: 'modalInstanceTwo',
            resolve: { message: () => message }
        });

        // No behaviour on close / dismiss.
        modalInstanceTwo.result.then(() => {}, () => {});
    };

    // Close create IOU modal dialogue.
    $scope.cancel = () => $uibModalInstance.dismiss();
});

// Controller for success/fail modal dialogue.
app.controller('messageCtrl', function ($uibModalInstance, message) {
    const modalInstanceTwo = this;
    modalInstanceTwo.message = message.data;
});