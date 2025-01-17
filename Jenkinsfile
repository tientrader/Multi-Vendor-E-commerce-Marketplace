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
                                sh 'docker build --platform linux/amd64 -t config-server:latest .'
                            }
                        }
                    }
                }
                stage('Build Docker Image for Service Discovery') {
                    steps {
                        dir('service-discovery') {
                            script {
                                sh 'docker build --platform linux/amd64 -t service-discovery:latest .'
                            }
                        }
                    }
                }
                stage('Build Docker Image for API Gateway') {
                    steps {
                        dir('api-gateway') {
                            script {
                                sh 'docker build --platform linux/amd64 -t api-gateway:latest .'
                            }
                        }
                    }
                }
                stage('Build Docker Image for User Service') {
                    steps {
                        dir('user-service') {
                            script {
                                sh 'docker build --platform linux/amd64 -t user-service:latest .'
                            }
                        }
                    }
                }
                stage('Build Docker Image for Shop Service') {
                    steps {
                        dir('shop-service') {
                            script {
                                sh 'docker build --platform linux/amd64 -t shop-service:latest .'
                            }
                        }
                    }
                }
                stage('Build Docker Image for Product Service') {
                    steps {
                        dir('product-service') {
                            script {
                                sh 'docker build --platform linux/amd64 -t product-service:latest .'
                            }
                        }
                    }
                }
                stage('Build Docker Image for Cart Service') {
                    steps {
                        dir('cart-service') {
                            script {
                                sh 'docker build --platform linux/amd64 -t cart-service:latest .'
                            }
                        }
                    }
                }
                stage('Build Docker Image for Order Service') {
                    steps {
                        dir('order-service') {
                            script {
                                sh 'docker build --platform linux/amd64 -t order-service:latest .'
                            }
                        }
                    }
                }
                stage('Build Docker Image for Post Service') {
                    steps {
                        dir('post-service') {
                            script {
                                sh 'docker build --platform linux/amd64 -t post-service:latest .'
                            }
                        }
                    }
                }
                stage('Build Docker Image for Notification Service') {
                    steps {
                        dir('notification-service') {
                            script {
                                sh 'docker build --platform linux/amd64 -t notification-service:latest .'
                            }
                        }
                    }
                }
                stage('Build Docker Image for Payment Service') {
                    steps {
                        dir('payment-service') {
                            script {
                                sh 'docker build --platform linux/amd64 -t payment-service:latest .'
                            }
                        }
                    }
                }
                stage('Build Docker Image for File Service') {
                    steps {
                        dir('file-service') {
                            script {
                                sh 'docker build --platform linux/amd64 -t file-service:latest .'
                            }
                        }
                    }
                }
                stage('Build Docker Image for Review Service') {
                    steps {
                        dir('review-service') {
                            script {
                                sh 'docker build --platform linux/amd64 -t review-service:latest .'
                            }
                        }
                    }
                }
                stage('Build Docker Image for Promotion Service') {
                    steps {
                        dir('promotion-service') {
                            script {
                                sh 'docker build --platform linux/amd64 -t promotion-service:latest .'
                            }
                        }
                    }
                }
            }
        }
        stage('Login and Push Docker Images to AWS ECR') {
            steps {
                script {
                    withCredentials([string(credentialsId: 'tienpro-id', variable: 'AWS_ACCESS_KEY_ID'),
                                     string(credentialsId: 'tienpro-secret', variable: 'AWS_SECRET_ACCESS_KEY')]) {
                        sh '''
                        aws ecr-public get-login-password --region us-east-1 | docker login --username AWS --password-stdin public.ecr.aws/k5o6t2c9
                        '''
                    }
                }
            }
        }
        stage('Push Docker Images to AWS ECR') {
            parallel {
                stage('Push Config Server Image') {
                    steps {
                        script {
                            sh 'docker tag config-server:latest public.ecr.aws/k5o6t2c9/e-com:config-server-v1.0'
                            sh 'docker push public.ecr.aws/k5o6t2c9/e-com:config-server-v1.0'
                        }
                    }
                }
                stage('Push Service Discovery Image') {
                    steps {
                        script {
                            sh 'docker tag service-discovery:latest public.ecr.aws/k5o6t2c9/e-com:service-discovery-v1.0'
                            sh 'docker push public.ecr.aws/k5o6t2c9/e-com:service-discovery-v1.0'
                        }
                    }
                }
                stage('Push API Gateway Image') {
                    steps {
                        script {
                            sh 'docker tag api-gateway:latest public.ecr.aws/k5o6t2c9/e-com:api-gateway-v1.0'
                            sh 'docker push public.ecr.aws/k5o6t2c9/e-com:api-gateway-v1.0'
                        }
                    }
                }
                stage('Push User Service Image') {
                    steps {
                        script {
                            sh 'docker tag user-service:latest public.ecr.aws/k5o6t2c9/e-com:user-service-v1.0'
                            sh 'docker push public.ecr.aws/k5o6t2c9/e-com:user-service-v1.0'
                        }
                    }
                }
                stage('Push Shop Service Image') {
                    steps {
                        script {
                            sh 'docker tag shop-service:latest public.ecr.aws/k5o6t2c9/e-com:shop-service-v1.0'
                            sh 'docker push public.ecr.aws/k5o6t2c9/e-com:shop-service-v1.0'
                        }
                    }
                }
                stage('Push Product Service Image') {
                    steps {
                        script {
                            sh 'docker tag product-service:latest public.ecr.aws/k5o6t2c9/e-com:product-service-v1.0'
                            sh 'docker push public.ecr.aws/k5o6t2c9/e-com:product-service-v1.0'
                        }
                    }
                }
                stage('Push Cart Service Image') {
                    steps {
                        script {
                            sh 'docker tag cart-service:latest public.ecr.aws/k5o6t2c9/e-com:cart-service-v1.0'
                            sh 'docker push public.ecr.aws/k5o6t2c9/e-com:cart-service-v1.0'
                        }
                    }
                }
                stage('Push Order Service Image') {
                    steps {
                        script {
                            sh 'docker tag order-service:latest public.ecr.aws/k5o6t2c9/e-com:order-service-v1.0'
                            sh 'docker push public.ecr.aws/k5o6t2c9/e-com:order-service-v1.0'
                        }
                    }
                }
                stage('Push Post Service Image') {
                    steps {
                        script {
                            sh 'docker tag post-service:latest public.ecr.aws/k5o6t2c9/e-com:post-service-v1.0'
                            sh 'docker push public.ecr.aws/k5o6t2c9/e-com:post-service-v1.0'
                        }
                    }
                }
                stage('Push Notification Service Image') {
                    steps {
                        script {
                            sh 'docker tag notification-service:latest public.ecr.aws/k5o6t2c9/e-com:notification-service-v1.0'
                            sh 'docker push public.ecr.aws/k5o6t2c9/e-com:notification-service-v1.0'
                        }
                    }
                }
                stage('Push Payment Service Image') {
                    steps {
                        script {
                            sh 'docker tag payment-service:latest public.ecr.aws/k5o6t2c9/e-com:payment-service-v1.0'
                            sh 'docker push public.ecr.aws/k5o6t2c9/e-com:payment-service-v1.0'
                        }
                    }
                }
                stage('Push File Service Image') {
                    steps {
                        script {
                            sh 'docker tag file-service:latest public.ecr.aws/k5o6t2c9/e-com:file-service-v1.0'
                            sh 'docker push public.ecr.aws/k5o6t2c9/e-com:file-service-v1.0'
                        }
                    }
                }
                stage('Push Review Service Image') {
                    steps {
                        script {
                            sh 'docker tag review-service:latest public.ecr.aws/k5o6t2c9/e-com:review-service-v1.0'
                            sh 'docker push public.ecr.aws/k5o6t2c9/e-com:review-service-v1.0'
                        }
                    }
                }
                stage('Push Promotion Service Image') {
                    steps {
                        script {
                            sh 'docker tag promotion-service:latest public.ecr.aws/k5o6t2c9/e-com:promotion-service-v1.0'
                            sh 'docker push public.ecr.aws/k5o6t2c9/e-com:promotion-service-v1.0'
                        }
                    }
                }
            }
        }
    }
}