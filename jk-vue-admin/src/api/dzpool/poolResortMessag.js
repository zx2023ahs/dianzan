import request from '@/utils/request'
export default {
    getList:function(params) {
        return request({
            url: '/dzpool/resort/messag/list',
            method: 'get',
            params
        })
    },
    add:function(params) {
        return request({
            url: '/dzpool/resort/messag',
            method: 'post',
            data: params
        })
    },
    update:function(params) {
        return request({
            url: '/dzpool/resort/messag',
            method: 'PUT',
            data: params
        })
    },
    remove:function(id) {
        return request({
            url: '/dzpool/resort/messag',
            method: 'delete',
            params: {
                id: id
            }
        })
    }
}
