import { createRouter, createWebHistory } from 'vue-router'
import PkIndexView from '../views/pk/PkIndexView'
import RecordIndexView from '../views/record/RecordIndexView'
import RanklistIndexView from '../views/ranklist/RanklistIndexView'
import UserIndexView from '../views/user/bots/UserbotIndexView'
import NotFound from "../views/error/NotFound"


const routes = [
  {
    path: "/",
    name: "home",
    redirect: "/pk/"
  },
  {
    path: '/pk/',
    name: "pk_index",
    component: PkIndexView,
  },
  {
    path: '/record/',
    name: "record_index",
    component: RecordIndexView,
  },
  {
    path: '/ranklist/',
    name: "ranklist_index",
    component: RanklistIndexView,
  },
  {
    path: '/user/bot/',
    name: "user_bots_index",
    component: UserIndexView,
  },
  {
    path: '/404/',
    name: "error_index",
    component: NotFound,
  },
  {
    path: '/:catchAll(.*)',
    redirect: '/404/'
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
