import ActionType from "./ActionType";
import Axios from "axios";
import {
    addToLocalCart,
    asyncActionFailure,
    asyncActionRequest,
    asyncActionSuccess,
    createTripSuccess,
    loadTripsSuccess,
    loadCategoryProductsSuccess,
    loadTripSuccess,
    loadOrderSuccess,
    loadProductSuccess,
    loadUserSuccess,
    logoutSuccess,
    publishMessage,
    removeFromLocalCart,
} from "./SyncActions";
import Routes from "../util/Routes";
import Routing from "../util/Routing";
import MockAdapter from "axios-mock-adapter";

const axios = Axios.create({withCredentials: true});

const SERVER_URL = process.env.REACT_APP_SERVER_URL || "";

/**
 * This part allows to mock server REST API so that the frontend can be developed without access to a real backend.
 */
if (process.env.REACT_APP_MOCK_REST_API) {
    // Mock backend REST API if the environment is configured to do so
    const mock = new MockAdapter(axios, {delayResponse: 200});
    // Mock current user data
    mock.onGet('/users/current').reply(200, {
        firstName: 'Catherine',
        lastName: 'Halsey',
        username: 'halsey@unsc.org',
        role: 'ADMIN'
    });
    // Mock login return value
    mock.onPost('/login').reply(200, {
        loggedIn: true,
        success: true
    });
}

/**
 * These Redux actions work asynchronously because they communicate with the backend. Their results are passed to the
 * Redux store via the dispatch function.
 */


export function loadTrips() {
    const action = {
        type: ActionType.LOAD_TRIPS
    };
    return (dispatch) => {
        dispatch(asyncActionRequest(action));
        return axios.get(`${SERVER_URL}/trips`)
            .then(resp => dispatch(loadTripsSuccess(resp.data)))
            .catch(error => {
                if (error.response.data.message) {
                    dispatch(publishMessage({message: error.response.data.message, type: 'danger'}));
                }
                return dispatch(asyncActionFailure(action, error.response.data));
            });
    };
}

export function loadTrip(tripID) {
    const action = {
        type: ActionType.LOAD_TRIPS
    };
    return dispatch => {
        dispatch(asyncActionRequest(action));
        return axios.get(`${SERVER_URL}/trips/${tripID}`)
            .then(resp => dispatch(loadTripSuccess(resp.data)))
            .catch(error => {
                if (error.response.data.message) {
                    dispatch(publishMessage({message: error.response.data.message, type: 'danger'}));
                }
                return dispatch(asyncActionFailure(action, error.response.data));
            });
    };
}

export function login(email, password) {
    const action = {
        type: ActionType.LOGIN
    };
    return dispatch => {
        dispatch(asyncActionRequest(action));
        const params = new URLSearchParams();
        params.append('username', email);
        params.append('password', password);
        return axios.post(`${SERVER_URL}login`, params)
            .then((resp) => {
                if (resp.data.loggedIn) {
                    dispatch(publishMessage({message: 'Login successful.', type: 'success'}));
                    return dispatch(loadUser());
                } else {
                    dispatch(asyncActionFailure(action, resp.data.message));
                    return dispatch(publishMessage({message: resp.data.message, type: 'danger'}));
                }
            })
            .catch(error => dispatch(asyncActionFailure(action, error.response.data)));
    };
}

export function loadUser() {
    const action = {
        type: ActionType.LOAD_USER
    };
    return dispatch => {
        dispatch(asyncActionRequest(action));
        return axios.get(`${SERVER_URL}/users/current`)
            .then(resp => dispatch(loadUserSuccess(resp.data)))
            .then(() => dispatch(loadCart()))
            .catch(error => {
                if (error.response.data.message) {
                    dispatch(publishMessage({message: error.response.data.message, type: 'danger'}));
                }
                return dispatch(asyncActionFailure(action, error.response.data));
            });
    };
}

export function logout() {
    const action = {
        type: ActionType.LOGOUT
    };
    return dispatch => {
        dispatch(asyncActionRequest(action));
        return axios.post(`${SERVER_URL}logout`)
            .then(() => dispatch(logoutSuccess()))
            .then(() => {
                Routing.transitionTo(Routes.home);
                window.location.reload();
                return dispatch(publishMessage({message: 'Logout successful.', type: 'success'}));
            })
            .catch(error => dispatch(asyncActionFailure(action, error.response.data)));
    };
}

export function register(profile) {
    const action = {
        type: ActionType.REGISTER
    };
    return dispatch => {
        dispatch(asyncActionRequest(action));
        return axios.post(`${SERVER_URL}/users`, profile)
            .then(() => dispatch(asyncActionSuccess(action)))
            .then(() => dispatch(publishMessage({message: 'Registration successful.', type: 'success'})))
            .then(() => dispatch(login(profile.username, profile.password)))
            .catch(error => {
                if (error.response.data.message) {
                    dispatch(publishMessage({message: error.response.data.message, type: 'danger'}));
                }
                dispatch(asyncActionFailure(action, error.response.data));
            });
    }
}

export function createTrip(name) {
    const action = {
        type: ActionType.CREATE_TRIP
    };
    return dispatch => {
        dispatch(asyncActionRequest(action));
        return axios.post(`${SERVER_URL}/trips`, {name})
            .then(() => dispatch(createTripSuccess()))
            .then(() => Routing.transitionTo(Routes.home))
            .then(() => dispatch(loadTrips()))
            .catch(error => {
                if (error.response.data.message) {
                    dispatch(publishMessage({message: error.response.data.message, type: 'danger'}));
                }
                dispatch(asyncActionFailure(action, error.response.data));
            });
    };
}

function extractIdFromLocation(resp) {
    const location = resp.headers['location'];
    return location.substring(location.lastIndexOf('/') + 1);
}

export function updateTrip(trip) {
    const action = {
        type: ActionType.UPDATE_TRIP
    };
    return dispatch => {
        dispatch(asyncActionRequest(action));
        return axios.put(`${SERVER_URL}/trips/${trip.id}`, trip)
            .then(() => dispatch(publishMessage({message: 'Trip updated.', type: 'success'})))
            .then(() => dispatch(asyncActionSuccess(action)))
            .then(() => dispatch(loadTrip(trip.id)))
            .catch(error => {
                if (error.response.data.message) {
                    dispatch(publishMessage({message: error.response.data.message, type: 'danger'}));
                }
                return dispatch(asyncActionFailure(action, error.response.data));
            });
    };
}

export function removeTrip(trip) {
    const action = {
        type: ActionType.REMOVE_TRIP
    };
    return dispatch => {
        dispatch(asyncActionRequest(action));
        return axios.delete(`${SERVER_URL}/trips/${trip.id}`)
            .then(() => dispatch(publishMessage({message: 'Trip removed.', type: 'success'})))
            .then(() => dispatch(asyncActionSuccess(action)))
            .then(() => Routing.transitionToHome())
            .catch(error => {
                if (error.response.data.message) {
                    dispatch(publishMessage({message: error.response.data.message, type: 'danger'}));
                }
                return dispatch(asyncActionFailure(action, error.response.data));
            });
    }
}
