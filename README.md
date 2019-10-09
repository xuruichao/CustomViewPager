<!-- toc -->

# 小程序多进程

## 简介

在Android端，小程序增加了多进程的功能，即每一个小程序都是一个单独的进程，并且使用单独的栈去维护。

## 多进程的优势

这样做的好处有以下几点：
1. 由于是单独进程，无论小程序内部因为何种原因的崩溃，对主进程都没有影响，增强用户体验。
2. 由于每个进程都有一片单独的内存区域，小程序不会占用主进程的内存，降低了内存溢出的风险。
3. 当用户希望在多个打开的小程序之间进行切换时，由于Activity都是用单独的栈去维护，因此用户可以在“最近使用过的程序”中找到其他小程序并实现切换。

## 多进程的实现思路

通过对BAT等大厂的小程序的研究，我们发现他们在程序中维护了5个不同的Activity，这些都是小程序的载体，每一个都定义了一个单独的进程和栈，在启动时，永远都是这5个Activity在循环，如果超过5个，最开始的那一个会被关闭。考虑到这些页面存在于不同的进程，并且需要进行通信，因此如何管理这些进程和栈，就成为了小程序多进程实现方案的关键。

C/S的架构模式比较适合此场景，将每个小程序的入口抽象为客户端，用一个统一的Server来管理所有的请求，如下图所示：

![avatar](CS.png)

1. Stub 跟 Proxy 是一对，俗称“代理-桩”。
2. Proxy 相当于是拿在手里的遥控器，而 Stub 相当于长在电视机里的遥控接收器，它们有着一一对应的接口方法，但操作的方向刚好相反。
3. Proxy 的接口供客户端程序调用，然后它内部会把信息包装好，以某种方式传递给 Stub，而后者通过对应的接口作用于服务端系统，从而完成了“远程调用”。

在Android中，使用Aidl来完成此业务场景的需求再合适不过了，不仅可以实现进程间的通信，还符合C/S的设计模式，方便统一处理信息和管理数据。下图是Aidl的工作模式：

![avatar](aidl.png)

在Server中，维护了一个进程和栈信息的数据关系，当用户有开启，关闭小程序等相关操作的时候，首先会响应到Server端，Server会根据现状去统一开启或销毁进程和栈，从而实现进程隔离的需求。

## 多进程和多任务栈的实现效果

![avatar](example1.jpg)

![avatar](example2.jpg)