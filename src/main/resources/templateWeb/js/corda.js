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

    $http.get(apiBaseURL + "me").then((response) => demoApp.thisNode = response.data.me);

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

    demoApp.openModalAmend = () => {
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

    demoApp.getAgreements = () => $http.get(apiBaseURL + "getAgreements")
        .then((response) => demoApp.ious = Object.keys(response.data)
            .map((key) => response.data[key].state.data)
            .reverse());

    demoApp.getAgreements();
});

app.controller('ModalInstanceCtrl', function ($scope, $http, $location, $uibModalInstance, $uibModal, apiBaseURL, peers) {
    const modalInstance = this;

    modalInstance.peers = peers;
    modalInstance.form = {};
    modalInstance.formError = false;

    // Validate and create IOU.
    modalInstance.create = () => {
		console.log('Called Create');
        if (invalidFormInput()) {
            modalInstance.formError = true;
        } else {
            modalInstance.formError = false;

            const agreement = {
                agrementName: modalInstance.form.agrementName,
				agreementValue: modalInstance.form.agreementValue,
				agrementInitiationDate: modalInstance.form.agrementInitiationDate,
				collateral: modalInstance.form.collateral
            };

            $uibModalInstance.close();
			
            const createIOUEndpoint = apiBaseURL + "initFlow/" +modalInstance.form.counterparty;

            // Create PO and handle success / fail responses.
            $http.put(createIOUEndpoint, angular.toJson(agreement)).then(
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

// Controller for success/fail modal dialogue.
app.controller('messageCtrl', function ($uibModalInstance, message) {
    const modalInstanceTwo = this;
    modalInstanceTwo.message = message.data;
});