pipeline {
    agent any
    tools {
        maven 'maven_3_9_9'
    }

    stages {
        stage('Build') {
            steps {
                git url: 'https://github.com/tientrader/Multi-Vendor-E-commerce-Marketplace', branch: 'main'
            }
        }
        stage('Build Services') {
            parallel {
		stage('Build Config Server') {
                    steps {
                        dir('config-server') {
                            sh 'mvn clean install'
                        }
                    }
                }
                stage('Build Service Discovery') {
                    steps {
                        dir('service-discovery') {
                            sh 'mvn clean install'
                        }
                    }
                }
                stage('Build API Gateway') {
                    steps {
                        dir('api-gateway') {
                            sh 'mvn clean install'
                        }
                    }
                }
                stage('Build User Service') {
                    steps {
                        dir('user-service') {
                            sh 'mvn clean install'
                        }
                    }
                }
                stage('Build Shop Service') {
                    steps {
                        dir('shop-service') {
                            sh 'mvn clean install'
                        }
                    }
                }
                stage('Build Product Service') {
                    steps {
                        dir('product-service') {
                            sh 'mvn clean install'
                        }
                    }
                }
                stage('Build Cart Service') {
                    steps {
                        dir('cart-service') {
                            sh 'mvn clean install'
                        }
                    }
                }
                stage('Build Order Service') {
                    steps {
                        dir('order-service') {
                            sh 'mvn clean install'
                        }
                    }
                }
                stage('Build Post Service') {
                    steps {
                        dir('post-service') {
                            sh 'mvn clean install'
                        }
                    }
                }
                stage('Build Notification Service') {
                    steps {
                        dir('notification-service') {
                            sh 'mvn clean install'
                        }
                    }
                }
                stage('Build Payment Service') {
                    steps {
                        dir('payment-service') {
                            sh 'mvn clean install'
                        }
                    }
                }
                stage('Build File Service') {
                    steps {
                        dir('file-service') {
                            sh 'mvn clean install'
                        }
                    }
                }
                stage('Build Review Service') {
                    steps {
                        dir('review-service') {
                            sh 'mvn clean install'
                        }
                    }
                }
                stage('Build Promotion Service') {
                    steps {
                        dir('promotion-service') {
                            sh 'mvn clean install'
                        }
                    }
                }
            }
        }
        stage('Build Docker Images') {
            parallel {
		        stage('Build Docker Image for Config Server') {
                    steps {
                        dir('config-server') {
                            script {
                                sh 'docker build -t config-server:latest .'
                            }
                        }
                    }
                }
                stage('Build Docker Image for Service Discovery') {
                    steps {
                        dir('service-discovery') {
                            script {
                                sh 'docker build -t service-discovery:latest .'
                            }
                        }
                    }
                }
                stage('Build Docker Image for API Gateway') {
                    steps {
                        dir('api-gateway') {
                            script {
                                sh 'docker build -t api-gateway:latest .'
                            }
                        }
                    }
                }
                stage('Build Docker Image for User Service') {
                    steps {
                        dir('user-service') {
                            script {
                                sh 'docker build -t user-service:latest .'
                            }
                        }
                    }
                }
                stage('Build Docker Image for Shop Service') {
                    steps {
                        dir('shop-service') {
                            script {
                                sh 'docker build -t shop-service:latest .'
                            }
                        }
                    }
                }
                stage('Build Docker Image for Product Service') {
                    steps {
                        dir('product-service') {
                            script {
                                sh 'docker build -t product-service:latest .'
                            }
                        }
                    }
                }
                stage('Build Docker Image for Cart Service') {
                    steps {
                        dir('cart-service') {
                            script {
                                sh 'docker build -t cart-service:latest .'
                            }
                        }
                    }
                }
                stage('Build Docker Image for Order Service') {
                    steps {
                        dir('order-service') {
                            script {
                                sh 'docker build -t order-service:latest .'
                            }
                        }
                    }
                }
                stage('Build Docker Image for Post Service') {
                    steps {
                        dir('post-service') {
                            script {
                                sh 'docker build -t post-service:latest .'
                            }
                        }
                    }
                }
                stage('Build Docker Image for Notification Service') {
                    steps {
                        dir('notification-service') {
                            script {
                                sh 'docker build -t notification-service:latest .'
                            }
                        }
                    }
                }
                stage('Build Docker Image for Payment Service') {
                    steps {
                        dir('payment-service') {
                            script {
                                sh 'docker build -t payment-service:latest .'
                            }
                        }
                    }
                }
                stage('Build Docker Image for File Service') {
                    steps {
                        dir('file-service') {
                            script {
                                sh 'docker build -t file-service:latest .'
                            }
                        }
                    }
                }
                stage('Build Docker Image for Review Service') {
                    steps {
                        dir('review-service') {
                            script {
                                sh 'docker build -t review-service:latest .'
                            }
                        }
                    }
                }
                stage('Build Docker Image for Promotion Service') {
                    steps {
                        dir('promotion-service') {
                            script {
                                sh 'docker build -t promotion-service:latest .'
                            }
                        }
                    }
                }
            }
        }
        stage('Login and Push Docker Images to Docker Hub') {
            steps {
                script {
                    withCredentials([string(credentialsId: 'tienpro', variable: 'tienpro')]) {
                        sh 'echo ${tienpro} | docker login -u ntiense03 --password-stdin'
                    }
                }
            }
        }
        stage('Push Docker Images to Docker Hub') {
            parallel {
		        stage('Push Config Server Image') {
                    steps {
                        script {
                            sh 'docker tag config-server:latest ntiense03/config-server:latest'
                            sh 'docker push ntiense03/config-server:latest'
                        }
                    }
                }
                stage('Push Service Discovery Image') {
                    steps {
                        script {
                            sh 'docker tag service-discovery:latest ntiense03/service-discovery:latest'
                            sh 'docker push ntiense03/service-discovery:latest'
                        }
                    }
                }
                stage('Push API Gateway Image') {
                    steps {
                        script {
                            sh 'docker tag api-gateway:latest ntiense03/api-gateway:latest'
                            sh 'docker push ntiense03/api-gateway:latest'
                        }
                    }
                }
                stage('Push User Service Image') {
                    steps {
                        script {
                            sh 'docker tag user-service:latest ntiense03/user-service:latest'
                            sh 'docker push ntiense03/user-service:latest'
                        }
                    }
                }
                stage('Push Shop Service Image') {
                    steps {
                        script {
                            sh 'docker tag shop-service:latest ntiense03/shop-service:latest'
                            sh 'docker push ntiense03/shop-service:latest'
                        }
                    }
                }
                stage('Push Product Service Image') {
                    steps {
                        script {
                            sh 'docker tag product-service:latest ntiense03/product-service:latest'
                            sh 'docker push ntiense03/product-service:latest'
                        }
                    }
                }
                stage('Push Cart Service Image') {
                    steps {
                        script {
                            sh 'docker tag cart-service:latest ntiense03/cart-service:latest'
                            sh 'docker push ntiense03/cart-service:latest'
                        }
                    }
                }
                stage('Push Order Service Image') {
                    steps {
                        script {
                            sh 'docker tag order-service:latest ntiense03/order-service:latest'
                            sh 'docker push ntiense03/order-service:latest'
                        }
                    }
                }
                stage('Push Post Service Image') {
                    steps {
                        script {
                            sh 'docker tag post-service:latest ntiense03/post-service:latest'
                            sh 'docker push ntiense03/post-service:latest'
                        }
                    }
                }
                stage('Push Notification Service Image') {
                    steps {
                        script {
                            sh 'docker tag notification-service:latest ntiense03/notification-service:latest'
                            sh 'docker push ntiense03/notification-service:latest'
                        }
                    }
                }
                stage('Push Payment Service Image') {
                    steps {
                        script {
                            sh 'docker tag payment-service:latest ntiense03/payment-service:latest'
                            sh 'docker push ntiense03/payment-service:latest'
                        }
                    }
                }
                stage('Push File Service Image') {
                    steps {
                        script {
                            sh 'docker tag file-service:latest ntiense03/file-service:latest'
                            sh 'docker push ntiense03/file-service:latest'
                        }
                    }
                }
                stage('Push Review Service Image') {
                    steps {
                        script {
                            sh 'docker tag review-service:latest ntiense03/review-service:latest'
                            sh 'docker push ntiense03/review-service:latest'
                        }
                    }
                }
                stage('Push Promotion Service Image') {
                    steps {
                        script {
                            sh 'docker tag promotion-service:latest ntiense03/promotion-service:latest'
                            sh 'docker push ntiense03/promotion-service:latest'
                        }
                    }
                }
            }
        }
    }
}