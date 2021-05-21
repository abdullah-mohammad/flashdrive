import { userReducer } from './user/userReducers'
import { createStore, applyMiddleware, combineReducers } from 'redux'
import thunk from "redux-thunk";
import { fileReducer } from './file/fileReducers';

const rootReducer = combineReducers({users: userReducer, files: fileReducer})

const store = createStore(rootReducer, applyMiddleware(thunk))

export default store