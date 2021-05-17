import { userReducer } from './user/userReducers'
import { createStore, applyMiddleware } from 'redux'
import thunk from "redux-thunk";

const store = createStore(userReducer, applyMiddleware(thunk))

export default store